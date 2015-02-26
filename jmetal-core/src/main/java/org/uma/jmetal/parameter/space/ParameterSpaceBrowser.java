package org.uma.jmetal.parameter.space;

public interface ParameterSpaceBrowser<Value> {

	public static enum BrowserMode {
		INCLUSION, EXCLUSION;

		public BrowserMode reverse() {
			if (this == INCLUSION) {
				return EXCLUSION;
			} else if (this == EXCLUSION) {
				return INCLUSION;
			} else {
				throw new RuntimeException("Unmanaged case: " + this);
			}
		}
	}

	public BrowserMode getMode();

	public boolean contains(Value value);
}
