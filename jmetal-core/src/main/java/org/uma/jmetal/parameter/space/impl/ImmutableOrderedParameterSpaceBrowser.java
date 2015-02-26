package org.uma.jmetal.parameter.space.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.uma.jmetal.parameter.space.OrderedParameterSpaceBrowser;

public class ImmutableOrderedParameterSpaceBrowser<Value extends Comparable<Value>>
		implements OrderedParameterSpaceBrowser<Value> {

	private final BrowserMode mode;
	private final Collection<Range<Value>> ranges;

	public ImmutableOrderedParameterSpaceBrowser(BrowserMode mode,
			Collection<Range<Value>> ranges) {
		this.mode = mode;
		HashSet<Range<Value>> tempRanges = new HashSet<>();
		for (Range<Value> range : ranges) {
			tempRanges.add(new ImmutableRange<>(range));
		}
		// TODO merge overlapping ranges
		this.ranges = tempRanges;
	}

	@Override
	public BrowserMode getMode() {
		return mode;
	}

	@Override
	public boolean contains(Value value) {
		for (Range<Value> range : ranges) {
			if (range.contains(value)) {
				return true;
			} else {
				// not found yet
			}
		}
		return false;
	}

	@Override
	public Iterator<Range<Value>> rangeIterator() {
		return ranges.iterator();
	}

}
