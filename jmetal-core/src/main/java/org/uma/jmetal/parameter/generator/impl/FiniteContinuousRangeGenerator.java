package org.uma.jmetal.parameter.generator.impl;

import org.uma.jmetal.parameter.generator.ValueGenerator;

public class FiniteContinuousRangeGenerator<Value> implements
		ValueGenerator<Value> {

	private Double ratio = null;
	private final SubGenerator<Value> generator;

	public FiniteContinuousRangeGenerator(SubGenerator<Value> generator) {
		this.generator = generator;
		if (generator.generate(0.0).equals(generator.generate(1.0))) {
			ratio = 0.0;
		} else {
			// keep default value
		}
	}

	public void setRatio(Double ratio) {
		if (ratio != null && (ratio < 0 || ratio > 1)) {
			throw new IllegalArgumentException(
					"The ratio should be between 0 and 1: " + ratio);
		} else {
			this.ratio = ratio;
		}
	}

	public Double getRatio() {
		return ratio;
	}

	@Override
	public boolean canGenerate() {
		return ratio != null && ratio >= 0 && ratio <= 1;
	}

	@Override
	public Value generate() {
		return generator.generate(ratio);
	}

	public static interface SubGenerator<Value> {
		public Value generate(double ratio);
	}
}
