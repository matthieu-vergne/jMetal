package org.uma.jmetal.experiment.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.uma.jmetal.experiment.ExperimentExecutor;
import org.uma.jmetal.experiment.impl.OneShotAlgorithmExecution.Runner;

/**
 * This {@link SequentialExperimentExecutor} runs the algorithms provided
 * sequentially in separated threads. The {@link #add(Object)} method is not
 * blocking: the {@link AlgorithmExecution} is created instantly and returned
 * after it has been added to a pool. It is run as soon as the previous ones
 * have been terminated, possibly a long time after the method has
 * returned.<br/>
 * <br/>
 * Because it is sequential, the same {@link Algorithm} instance is reused if
 * multiple runs are requested through {@link #add(Algorithm)}.<br/>
 * <br/>
 * The threads created are daemons, so they automatically stop with the main
 * thread (as long as it is the only non-deamon thread remaining).
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 *
 */
public class SequentialExperimentExecutor<Algorithm> implements ExperimentExecutor<Algorithm> {

	private final List<AlgorithmExecution<Algorithm>> remainingExecutions = new LinkedList<>();
	private final Runner<Algorithm> runner;
	private final ExecutorService executor;
	private boolean isStarted = false;

	public SequentialExperimentExecutor(Runner<Algorithm> runner) {
		this.runner = runner;

		ThreadFactory factory = (runnable) -> {
			Thread thread = new Thread(runnable);
			thread.setDaemon(true);
			return thread;
		};
		executor = Executors.newFixedThreadPool(1, factory);
		executor.submit(new Runnable() {

			@Override
			public void run() {
				synchronized (executor) {
					if (executor.isShutdown()) {
						return;
					} else if (!isStarted || remainingExecutions.isEmpty()) {
						try {
							executor.submit(this);
							executor.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						executor.submit(remainingExecutions.remove(0));
						executor.submit(this);
					}
				}
			}
		});
	}

	@Override
	public AlgorithmExecution<Algorithm> add(Algorithm algorithm) {
		OneShotAlgorithmExecution<Algorithm> execution = new OneShotAlgorithmExecution<Algorithm>(algorithm, runner);
		remainingExecutions.add(execution);
		synchronized (executor) {
			executor.notify();
		}
		return execution;
	}

	public Collection<AlgorithmExecution<Algorithm>> addAll(Iterable<Algorithm> algorithms) {
		Collection<AlgorithmExecution<Algorithm>> executions = new LinkedList<>();
		for (Algorithm algorithm : algorithms) {
			executions.add(add(algorithm));
		}
		return executions;
	}

	@Override
	public void start() {
		isStarted = true;
		synchronized (executor) {
			executor.notify();
		}
	}

	public void stop() {
		isStarted = false;
	}

	@Override
	public boolean isStarted() {
		return isStarted;
	}

	@Override
	protected void finalize() throws Throwable {
		synchronized (executor) {
			executor.shutdown();
			executor.notifyAll();
		}
	}
}
