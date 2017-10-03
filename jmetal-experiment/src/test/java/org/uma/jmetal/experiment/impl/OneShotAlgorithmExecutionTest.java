package org.uma.jmetal.experiment.impl;

import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution;
import org.uma.jmetal.experiment.AlgorithmExecutionTest;

public class OneShotAlgorithmExecutionTest extends AlgorithmExecutionTest {

	@Override
	public <Algorithm extends Runnable> AlgorithmExecution<Algorithm> createAlgorithmExecution(Algorithm algorithm) {
		return new OneShotAlgorithmExecution<Algorithm>(algorithm, (algo) -> algo.run());
	}

}
