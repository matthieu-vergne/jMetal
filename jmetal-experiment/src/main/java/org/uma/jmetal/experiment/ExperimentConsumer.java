package org.uma.jmetal.experiment;

import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution;

/**
 * An {@link ExperimentConsumer} aims at exploiting the data generated during
 * {@link AlgorithmExecution}s. It usually aims at reading relevant data to
 * provide them to some interfaces, compute statistics, or build reports.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 *
 */
public interface ExperimentConsumer<Algorithm> {

	/**
	 * This method is called when an {@link AlgorithmExecution} is expected to
	 * be run. After this method has returned, the {@link ExperimentConsumer}
	 * should have set up ways to obtain relevant data, like registering
	 * listeners.
	 * 
	 * @param execution
	 *            the {@link AlgorithmExecution} to watch
	 */
	void watch(AlgorithmExecution<Algorithm> execution);

}
