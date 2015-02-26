package org.uma.jmetal.parameter.space.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import org.uma.jmetal.parameter.space.ExplicitParameterSpaceBrowser;

public class ImmutableExplicitParameterSpaceBrowser<Value> implements
		ExplicitParameterSpaceBrowser<Value> {

	private final BrowserMode mode;
	private final Collection<Value> values;

	public ImmutableExplicitParameterSpaceBrowser(BrowserMode mode,
			Collection<Value> values) {
		this.mode = mode;
		this.values = Collections.unmodifiableCollection(new HashSet<>(values));
	}

	@Override
	public BrowserMode getMode() {
		return mode;
	}
	
	@Override
	public Collection<Value> getAll() {
		return values;
	}

	@Override
	public boolean contains(Value value) {
		return values.contains(value);
	}

	@Override
	public Iterator<Value> iterator() {
		return values.iterator();
	}

}
