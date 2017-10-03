package org.uma.jmetal.experiment.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution;
import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution.AlgorithmExecutionListener;
import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution.RunStatus;

/**
 * This class provides a simple way to retrieve statistical data about a given
 * {@link ExperimentRunSuite}.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 *
 */
// TODO test
// TODO add time counter (based on Measure when available)
public class ExperimentSummarizer<Algorithm> implements Iterable<AlgorithmExecution<Algorithm>> {
	private final Map<RunStatus, Collection<AlgorithmExecution<Algorithm>>> counters = new HashMap<>();
	private final Map<AlgorithmExecution<Algorithm>, AlgorithmExecutionListener> listeners = new HashMap<>();

	public ExperimentSummarizer() {
		for (RunStatus status : RunStatus.values()) {
			counters.put(status, new HashSet<>());
		}
	}

	public void add(AlgorithmExecution<Algorithm> execution) {
		if (listeners.containsKey(execution)) {
			// already added
		} else {
			RunStatus status = execution.getRunStatus();
			synchronized (counters) {
				counters.get(status).add(execution);
			}
			AlgorithmExecutionListener listener = new AlgorithmExecutionListener() {
				RunStatus previous = status;

				@Override
				public void hasChangedStatus(RunStatus newStatus) {
					synchronized (counters) {
						counters.get(previous).remove(execution);
						counters.get(newStatus).add(execution);
					}
					previous = newStatus;
				}
			};
			execution.addRunListener(listener);
			listeners.put(execution, listener);
		}
	}

	public void remove(AlgorithmExecution<Algorithm> execution) {
		if (!listeners.containsKey(execution)) {
			// not added
		} else {
			execution.removeRunListener(listeners.get(execution));
			synchronized (counters) {
				RunStatus status = execution.getRunStatus();
				counters.get(status).remove(execution);
			}
		}
	}

	public void add(Iterable<AlgorithmExecution<Algorithm>> executions) {
		for (AlgorithmExecution<Algorithm> execution : executions) {
			add(execution);
		}
	}

	public void remove(Iterable<AlgorithmExecution<Algorithm>> executions) {
		for (AlgorithmExecution<Algorithm> execution : executions) {
			remove(execution);
		}
	}

	public int size() {
		return listeners.size();
	}

	public int size(RunStatus status) {
		synchronized (counters) {
			return counters.get(status).size();
		}
	}

	public Collection<AlgorithmExecution<Algorithm>> getExecutions(RunStatus status) {
		synchronized (counters) {
			return new HashSet<>(counters.get(status));
		}
	}

	@Override
	public Iterator<AlgorithmExecution<Algorithm>> iterator() {
		return listeners.keySet().iterator();
	}
}
