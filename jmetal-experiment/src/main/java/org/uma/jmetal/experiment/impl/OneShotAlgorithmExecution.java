package org.uma.jmetal.experiment.impl;

import java.util.Collection;
import java.util.HashSet;

import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution;

/**
 * A {@link OneShotAlgorithmExecution} runs its {@link Algorithm} until the end
 * in one shot. No {@link RunStatus#PAUSED} is managed.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public class OneShotAlgorithmExecution<Algorithm> implements AlgorithmExecution<Algorithm> {

	private final Algorithm algorithm;
	private final Runner<Algorithm> runner;
	private final Collection<AlgorithmExecutionListener> listeners;
	private RunStatus runStatus;

	public OneShotAlgorithmExecution(Algorithm algorithm, Runner<Algorithm> runner) {
		if (algorithm == null) {
			throw new NullPointerException("No algorithm provided");
		} else {
			this.algorithm = algorithm;
			this.runStatus = RunStatus.WAITING;
			this.listeners = new HashSet<>();
			this.runner = runner;
		}
	}

	public interface Runner<Algorithm> {
		public void run(Algorithm algorithm);
	}

	@Override
	public Algorithm getAlgorithm() {
		return algorithm;
	}

	@Override
	public void run() throws AlreadyRunException {
		if (runStatus != RunStatus.WAITING) {
			throw new AlreadyRunException(this);
		} else {
			changeStatus(RunStatus.RUNNING);
			try {
				runner.run(algorithm);
				changeStatus(RunStatus.TERMINATED);
			} catch (Exception e) {
				changeStatus(RunStatus.INTERRUPTED);
				throw e;
			}
		}
	}

	private void changeStatus(RunStatus status) {
		runStatus = status;
		for (AlgorithmExecutionListener listener : listeners) {
			listener.hasChangedStatus(runStatus);
		}
	}

	@Override
	public RunStatus getRunStatus() {
		return runStatus;
	}

	@Override
	public void addRunListener(AlgorithmExecutionListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeRunListener(AlgorithmExecutionListener listener) {
		listeners.remove(listener);
	}

}
