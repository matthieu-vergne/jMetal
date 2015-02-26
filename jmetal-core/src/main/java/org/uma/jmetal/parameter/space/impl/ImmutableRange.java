package org.uma.jmetal.parameter.space.impl;

import org.uma.jmetal.parameter.space.OrderedParameterSpaceBrowser.Range;

public class ImmutableRange<Value extends Comparable<Value>> implements
		Range<Value> {

	private final Value min;
	private final Value max;

	public ImmutableRange(Value min, Value max) {
		if (min == null) {
			throw new IllegalArgumentException("No min provided");
		} else if (max == null) {
			throw new IllegalArgumentException("No max provided");
		} else if (min.compareTo(max) > 0) {
			throw new IllegalArgumentException("Min and max are reversed: "
					+ min + " > " + max);
		} else {
			this.min = min;
			this.max = max;
		}
	}

	public ImmutableRange(Range<Value> range) {
		this(range.getMin(), range.getMax());
	}

	@Override
	public Value getMin() {
		return min;
	}

	@Override
	public Value getMax() {
		return max;
	}

	@Override
	public boolean contains(Value value) {
		return value != null && min.compareTo(value) <= 0
				&& max.compareTo(value) >= 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof Range) {
			Range<?> r = (Range<?>) obj;
			return min.equals(r.getMin()) && max.equals(r.getMax());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return min.hashCode() * max.hashCode();
	}
}
