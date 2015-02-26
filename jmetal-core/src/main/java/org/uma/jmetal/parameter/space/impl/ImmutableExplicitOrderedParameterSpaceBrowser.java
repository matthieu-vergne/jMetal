package org.uma.jmetal.parameter.space.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.uma.jmetal.parameter.space.ExplicitParameterSpaceBrowser;
import org.uma.jmetal.parameter.space.OrderedParameterSpaceBrowser;

public class ImmutableExplicitOrderedParameterSpaceBrowser<Value extends Comparable<Value>>
		extends ImmutableOrderedParameterSpaceBrowser<Value> implements
		ExplicitParameterSpaceBrowser<Value>,
		OrderedParameterSpaceBrowser<Value> {

	public static interface Incrementer<Value> {
		public Value next(Value value);
	}

	private final Incrementer<Value> incrementer;

	public ImmutableExplicitOrderedParameterSpaceBrowser(BrowserMode mode,
			Collection<Range<Value>> ranges, Incrementer<Value> incrementer) {
		super(mode, ranges);
		this.incrementer = incrementer;
	}

	private Collection<Value> allValues = null;

	@Override
	public Collection<Value> getAll() {
		if (allValues == null) {
			allValues = new HashSet<>();
			for (Value value : this) {
				allValues.add(value);
			}
			allValues = Collections.unmodifiableCollection(allValues);
		} else {
			// reuse already cached values
		}
		return allValues;
	}

	@Override
	public Iterator<Value> iterator() {
		if (allValues != null) {
			return allValues.iterator();
		} else {
			return new Iterator<Value>() {

				private final Iterator<Range<Value>> rangeIterator = rangeIterator();
				private Range<Value> currentRange = null;
				private Value current = null;
				private Value next = null;

				@Override
				public boolean hasNext() {
					searchNextIfNeeded();
					return next != null;
				}

				private void searchNextIfNeeded() {
					if (current == null && next == null) {
						// TODO factor with code below
						if (rangeIterator.hasNext()) {
							currentRange = rangeIterator.next();
							next = currentRange.getMin();
						} else {
							// no range to browse
						}
					} else if (next == null) {
						if (currentRange.getMax().compareTo(current) > 0) {
							next = incrementer.next(current);
						} else {
							// TODO factor with code above
							if (rangeIterator.hasNext()) {
								currentRange = rangeIterator.next();
								next = currentRange.getMin();
							} else {
								// no other range to browse
							}
						}
					} else if (next != null) {
						// already found
					} else {
						throw new RuntimeException("Unmanaged case.");
					}
				}

				@Override
				public Value next() {
					searchNextIfNeeded();
					if (!hasNext()) {
						throw new NoSuchElementException();
					} else {
						return next;
					}
				}

				@Override
				public void remove() {
					throw new RuntimeException(
							"You cannot remove a value from this browser.");
				}
			};
		}
	}

}
