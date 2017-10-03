package org.uma.jmetal.experiment.testUtil;

public class Sum extends Computer {

	public float sum1;
	public float sum2;

	@Override
	public float compute() {
		return sum1 + sum2;
	}
}
