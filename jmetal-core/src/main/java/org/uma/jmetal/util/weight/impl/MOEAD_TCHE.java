package org.uma.jmetal.util.weight.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.weight.WeightingStrategy;

public class MOEAD_TCHE<S extends Solution<?>> implements
		WeightingStrategy<S, Double> {
	private final double[] idealPoint;
	private final double[] lambda;
	private final int total;

	public MOEAD_TCHE(double[] idealPoint, double[] lambda, int total) {
		this.idealPoint = idealPoint;
		this.lambda = lambda;
		this.total = total;
	}

	@Override
	public Double weight(S individual) {
		double maxFun = -1.0e+30;

		for (int n = 0; n < total; n++) {
			double diff = Math.abs(individual.getObjective(n) - idealPoint[n]);

			double feval;
			if (lambda[n] == 0) {
				feval = 0.0001 * diff;
			} else {
				feval = diff * lambda[n];
			}
			if (feval > maxFun) {
				maxFun = feval;
			}
		}

		return maxFun;
	}

}
