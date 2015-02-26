package org.uma.jmetal.parameter.space;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.uma.jmetal.parameter.space.ParameterSpaceBrowser.BrowserMode;
import org.uma.jmetal.parameter.space.impl.ImmutableExplicitParameterSpaceBrowser;

public class ParameterSpaceFactory {

	private final ParameterSpaceBrowserFactory browserFactory = new ParameterSpaceBrowserFactory();

	public <Value> ParameterSpace<Value> createEmptySpace(
			final boolean isNullAccepted) {
		Collection<Value> values = isNullAccepted ? Arrays
				.<Value> asList((Value) null) : Collections.<Value> emptyList();
		final ParameterSpaceBrowser<Value> browser = new ImmutableExplicitParameterSpaceBrowser<>(
				BrowserMode.INCLUSION, values);
		return new ParameterSpace<Value>() {

			@Override
			public boolean contains(Value value) {
				return browser.contains(value);
			}

			@Override
			public ParameterSpaceBrowser<Value> getBrowser() {
				return browser;
			}
		};
	}

	public <Value> ParameterSpace<Value> createFullSpace(
			final boolean isNullAccepted) {
		Collection<Value> values = !isNullAccepted ? Arrays
				.<Value> asList((Value) null) : Collections.<Value> emptyList();
		final ParameterSpaceBrowser<Value> browser = new ImmutableExplicitParameterSpaceBrowser<>(
				BrowserMode.EXCLUSION, values);
		return new ParameterSpace<Value>() {

			@Override
			public boolean contains(Value value) {
				return !browser.contains(value);
			}

			@Override
			public ParameterSpaceBrowser<Value> getBrowser() {
				return browser;
			}
		};
	}

	@SuppressWarnings("unchecked")
	public <Value> ParameterSpace<Value> createExplicitSpace(Value... values) {
		return createExplicitSpace(Arrays.asList(values));
	}

	public <Value> ParameterSpace<Value> createExplicitSpace(
			final Collection<Value> values) {
		final ParameterSpaceBrowser<Value> browser = new ImmutableExplicitParameterSpaceBrowser<>(
				BrowserMode.INCLUSION, values);
		return new ParameterSpace<Value>() {

			@Override
			public boolean contains(Value value) {
				return browser.contains(value);
			}

			@Override
			public ParameterSpaceBrowser<Value> getBrowser() {
				return browser;
			}
		};
	}

	public <Value> ParameterSpace<Value> createComplementSpace(
			final ParameterSpace<Value> space) {
		final ParameterSpaceBrowser<Value> browser = browserFactory
				.createComplementBrowser(space.getBrowser());
		return new ParameterSpace<Value>() {

			@Override
			public boolean contains(Value value) {
				if (browser.getMode() == BrowserMode.INCLUSION) {
					return browser.contains(value);
				} else if (browser.getMode() == BrowserMode.EXCLUSION) {
					return !browser.contains(value);
				} else {
					throw new RuntimeException("Unmanaged case.");
				}
			}

			@Override
			public ParameterSpaceBrowser<Value> getBrowser() {
				return browser;
			}
		};
	}

	public <Value, V1 extends Value, V2 extends Value> ParameterSpace<Value> createUnionSpace(
			final ParameterSpace<V1> space1, final ParameterSpace<V2> space2) {
		return new ParameterSpace<Value>() {

			@SuppressWarnings("unchecked")
			@Override
			public boolean contains(Value value) {
				/*
				 * TODO Consider optimizing performance by running both
				 * evaluations in parallel in separate threads (with Future)
				 * and, if one returns true, stop the other and return true.
				 * Otherwise, wait and return the last one. Don't forget that,
				 * because spaces can be nested, sub-spaces can also launch
				 * their own threads, and so on. Be sure to give priority to
				 * threads run in sub-spaces.
				 */
				boolean c1;
				try {
					c1 = space1.contains((V1) value);
				} catch (ClassCastException e) {
					c1 = false;
				}
				if (c1) {
					return true;
				} else {
					boolean c2;
					try {
						c2 = space2.contains((V2) value);
					} catch (ClassCastException e) {
						c2 = false;
					}
					return c2;
				}
			}

			@Override
			public ParameterSpaceBrowser<Value> getBrowser() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	public <Value, V1 extends Value, V2 extends Value> ParameterSpace<Value> createIntersectionSpace(
			final ParameterSpace<V1> space1, final ParameterSpace<V2> space2) {
		return new ParameterSpace<Value>() {

			@SuppressWarnings("unchecked")
			@Override
			public boolean contains(Value value) {
				/*
				 * TODO Consider optimizing performance by running both
				 * evaluations in parallel in separate threads (with Future)
				 * and, if one returns false, stop the other and return false.
				 * Otherwise, wait and return the last one. Don't forget that,
				 * because spaces can be nested, sub-spaces can also launch
				 * their own threads, and so on. Be sure to give priority to
				 * threads run in sub-spaces.
				 */
				boolean c1;
				try {
					c1 = space1.contains((V1) value);
				} catch (ClassCastException e) {
					c1 = false;
				}
				if (!c1) {
					return false;
				} else {
					boolean c2;
					try {
						c2 = space2.contains((V2) value);
					} catch (ClassCastException e) {
						c2 = false;
					}
					return c2;
				}
			}

			@Override
			public ParameterSpaceBrowser<Value> getBrowser() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
}
