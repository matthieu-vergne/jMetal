package org.uma.jmetal.experiment.testUtil;

public class Product extends Computer {

	public float product1;
	public float product2;

	@Override
	public float compute() {
		return product1 * product2;
	}

}
