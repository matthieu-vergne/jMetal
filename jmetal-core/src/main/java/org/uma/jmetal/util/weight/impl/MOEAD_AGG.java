package org.uma.jmetal.util.weight.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.weight.WeightingStrategy;

public class MOEAD_AGG<S extends Solution<?>> implements
		WeightingStrategy<S, Double> {
	private final double[] lambda;
	private final int total;

	public MOEAD_AGG(double[] lambda, int total) {
		this.lambda = lambda;
		this.total = total;
	}

	@Override
	public Double weight(S individual) {
		double sum = 0.0;
		for (int n = 0; n < total; n++) {
			sum += (lambda[n]) * individual.getObjective(n);
		}

		return sum;
	}

}
