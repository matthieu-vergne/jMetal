package org.uma.jmetal.experiment;

import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution;

/**
 * A {@link FECExperiment} allows to compare algorithms to assess their
 * performance. It can be useful when we design a new algorithm and we want to
 * compare it with others of the state-of-the-art, or when we design a component
 * (e.g., an operator) and we want to compare a number of versions of a same
 * algorithms with different component variants.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public interface FECExperiment<Algorithm> extends Experiment {
	public ExperimentFeeder<Algorithm> getExperimentFeeder();

	public ExperimentExecutor<Algorithm> getExperimentExecutor();

	public ExperimentConsumer<Algorithm> getExperimentConsumer();

	@Override
	default void run() {
		ExperimentExecutor<Algorithm> executor = getExperimentExecutor();
		ExperimentConsumer<Algorithm> consumer = getExperimentConsumer();
		for (Algorithm algo : getExperimentFeeder()) {
			AlgorithmExecution<Algorithm> execution = executor.add(algo);
			consumer.watch(execution);
		}
		executor.start();
	}
}
