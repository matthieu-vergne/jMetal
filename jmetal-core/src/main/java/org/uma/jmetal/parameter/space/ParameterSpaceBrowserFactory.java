package org.uma.jmetal.parameter.space;

import org.uma.jmetal.parameter.space.impl.ImmutableExplicitParameterSpaceBrowser;
import org.uma.jmetal.parameter.space.impl.ImmutableOrderedParameterSpaceBrowser;

public class ParameterSpaceBrowserFactory {

	public <Value> ParameterSpaceBrowser<Value> createComplementBrowser(
			ParameterSpaceBrowser<Value> browser) {
		boolean isExplicit = browser instanceof ExplicitParameterSpaceBrowser;
		boolean isOrdered = browser instanceof OrderedParameterSpaceBrowser;
		if (isExplicit) {
			ExplicitParameterSpaceBrowser<Value> explicit = (ExplicitParameterSpaceBrowser<Value>) browser;
			return new ImmutableExplicitParameterSpaceBrowser<>(explicit
					.getMode().reverse(), explicit.getAll());
		} else if (isOrdered) {
			OrderedParameterSpaceBrowser<Value> ordered = (OrderedParameterSpaceBrowser<Value>) browser;
			return new ImmutableOrderedParameterSpaceBrowser<>(ordered.getMode().reverse(), ranges);
		} else {
			// TODO
		}
	}
}
