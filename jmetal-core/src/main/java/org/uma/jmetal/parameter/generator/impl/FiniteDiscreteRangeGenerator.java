package org.uma.jmetal.parameter.generator.impl;

import org.uma.jmetal.parameter.generator.ValueGenerator;

public class FiniteDiscreteRangeGenerator<Value> implements
		ValueGenerator<Value> {

	private Integer index = null;
	private final int rangeLength;
	private final SubGenerator<Value> generator;

	public FiniteDiscreteRangeGenerator(int rangeLength,
			SubGenerator<Value> generator) {
		this.rangeLength = rangeLength;
		this.generator = generator;
		if (rangeLength == 1) {
			index = 0;
		} else {
			index = null;
		}
	}

	public void setIndex(Integer index) {
		if (index != null && (index < 0 || index > getMaxIndex())) {
			throw new IllegalArgumentException(
					"The index should be between 0 and " + getMaxIndex() + ": "
							+ index);
		} else {
			this.index = index;
		}
	}

	public Integer getIndex() {
		return index;
	}

	public int getMaxIndex() {
		return rangeLength - 1;
	}

	@Override
	public boolean canGenerate() {
		return index != null && index >= 0 && index <= getMaxIndex();
	}

	@Override
	public Value generate() {
		return generator.generate(index);
	}

	public static interface SubGenerator<Value> {
		public Value generate(int index);
	}
}
