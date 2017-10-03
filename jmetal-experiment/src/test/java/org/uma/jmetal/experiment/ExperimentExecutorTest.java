package org.uma.jmetal.experiment;

import static org.junit.Assert.*;

import org.junit.Test;
import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution;
import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution.RunStatus;
import org.uma.jmetal.experiment.testUtil.Sum;

public abstract class ExperimentExecutorTest extends AlgorithmExecutionTest {

	public abstract <Algorithm> ExperimentExecutor<Algorithm> createExecutor();

	@Override
	public <Algorithm extends Runnable> AlgorithmExecution<Algorithm> createAlgorithmExecution(Algorithm algorithm) {
		return this.<Algorithm>createExecutor().add(algorithm);
	}

	@Test
	public void testAddReturnsWaitingRun() {
		ExperimentExecutor<Runnable> executor = createExecutor();

		AlgorithmExecution<Runnable> execution = executor.add(() -> {
			/* Do nothing */});
		assertNotNull(execution);
		assertEquals(RunStatus.WAITING, execution.getRunStatus());
	}

	@Test
	public void testRunStartsWithExecutor() throws InterruptedException {
		ExperimentExecutor<Runnable> executor = createExecutor();
		AlgorithmExecution<Runnable> execution = executor.add(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		Thread.sleep(100);
		assertEquals(RunStatus.WAITING, execution.getRunStatus());
		executor.start();
		Thread.sleep(10);
		assertEquals(RunStatus.RUNNING, execution.getRunStatus());
	}

	@Test
	public void testIsStartedReturnsCorrectValue() throws InterruptedException {
		ExperimentExecutor<Runnable> executor = createExecutor();

		assertFalse(executor.isStarted());
		executor.start();
		assertTrue(executor.isStarted());
	}

	@Test
	public void testExecutionProvidesAddedAlgorithm() throws InterruptedException {
		ExperimentExecutor<Sum> executor = createExecutor();
		Sum algorithm = new Sum();
		AlgorithmExecution<Sum> execution = executor.add(algorithm);

		assertSame(algorithm, execution.getAlgorithm());
	}

}
