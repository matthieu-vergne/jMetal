package org.uma.jmetal.parameter.space;

import java.util.Iterator;

public interface OrderedParameterSpaceBrowser<Value extends Comparable<Value>>
		extends ParameterSpaceBrowser<Value> {

	public static interface Range<Value extends Comparable<Value>> {
		public Value getMin();

		public Value getMax();
		
		public boolean contains(Value value);
	}

	public Iterator<Range<Value>> rangeIterator();
}
