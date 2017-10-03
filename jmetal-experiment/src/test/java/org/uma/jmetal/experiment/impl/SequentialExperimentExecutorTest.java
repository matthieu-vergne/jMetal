package org.uma.jmetal.experiment.impl;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.uma.jmetal.experiment.ExperimentExecutor;
import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution;
import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution.RunStatus;
import org.uma.jmetal.experiment.ExperimentExecutorTest;

public class SequentialExperimentExecutorTest extends ExperimentExecutorTest {

	@Override
	public <Algorithm> ExperimentExecutor<Algorithm> createExecutor() {
		return new ExperimentExecutor<Algorithm>() {

			SequentialExperimentExecutor<Runnable> executor = new SequentialExperimentExecutor<Runnable>(
					(algo) -> algo.run());

			@Override
			public AlgorithmExecution<Algorithm> add(Algorithm algorithm) {
				AlgorithmExecution<Runnable> execution;
				if (algorithm instanceof Runnable) {
					execution = executor.add((Runnable) algorithm);
				} else {
					execution = executor.add(new Runnable() {

						@Override
						public void run() {
							throw new RuntimeException("No information on how to run algorithm " + algorithm);
						}
					});
				}
				return new AlgorithmExecution<Algorithm>() {

					@Override
					public Algorithm getAlgorithm() {
						return algorithm;
					}

					@Override
					public void run() throws AlreadyRunException {
						execution.run();
					}

					@Override
					public RunStatus getRunStatus() {
						return execution.getRunStatus();
					}

					@Override
					public void addRunListener(AlgorithmExecutionListener listener) {
						execution.addRunListener(listener);
					}

					@Override
					public void removeRunListener(AlgorithmExecutionListener listener) {
						execution.removeRunListener(listener);
					}

				};
			}

			@Override
			public void start() throws AlreadyStartedException {
				executor.start();
			}

			@Override
			public boolean isStarted() {
				return executor.isStarted();
			}
		};
	}

	@Test
	public void testAddAllProperlyAdds() throws InterruptedException {
		List<Runnable> algorithms = new LinkedList<Runnable>();
		int count = 10;
		boolean[] isRun = new boolean[count];
		for (int i = 0; i < count; i++) {
			int index = i;
			isRun[index] = false;
			algorithms.add(() -> isRun[index] = true);
		}

		SequentialExperimentExecutor<Runnable> executor = new SequentialExperimentExecutor<Runnable>(
				(algo) -> algo.run());
		List<AlgorithmExecution<Runnable>> executions = new LinkedList<>(executor.addAll(algorithms));

		executor.start();

		long start = System.currentTimeMillis();
		while (!executions.isEmpty()) {
			if (executions.get(0).getRunStatus() == RunStatus.TERMINATED) {
				executions.remove(0);
			} else if (System.currentTimeMillis() - start > 10000) {
				fail("Too long to terminate");
			} else {
				Thread.sleep(10);
			}
		}
	}
}
