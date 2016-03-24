package org.uma.jmetal.util.weight.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.weight.WeightingStrategy;

public class MOEAD_PBI<S extends Solution<?>> implements
		WeightingStrategy<S, Double> {
	private final double[] idealPoint;
	private final double[] lambda;
	private final int total;

	public MOEAD_PBI(double[] idealPoint, double[] lambda, int total) {
		this.idealPoint = idealPoint;
		this.lambda = lambda;
		this.total = total;
	}

	@Override
	public Double weight(S individual) {
		double d1, d2, nl;
		double theta = 5.0;

		d1 = d2 = nl = 0.0;

		for (int i = 0; i < total; i++) {
			d1 += (individual.getObjective(i) - idealPoint[i]) * lambda[i];
			nl += Math.pow(lambda[i], 2.0);
		}
		nl = Math.sqrt(nl);
		d1 = Math.abs(d1) / nl;

		for (int i = 0; i < total; i++) {
			d2 += Math.pow((individual.getObjective(i) - idealPoint[i]) - d1
					* (lambda[i] / nl), 2.0);
		}
		d2 = Math.sqrt(d2);

		return (d1 + theta * d2);
	}

}
