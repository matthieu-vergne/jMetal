package org.uma.jmetal.parameter.space;

import java.util.Collection;
import java.util.Iterator;

public interface ExplicitParameterSpaceBrowser<Value> extends ParameterSpaceBrowser<Value>, Iterable<Value> {

	public Collection<Value> getAll();
	
	@Override
	public Iterator<Value> iterator();
}
