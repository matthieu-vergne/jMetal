package org.uma.jmetal.parameter;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.parameter.space.ParameterSpace;

/**
 * A {@link Parameter} aims at describing the {@link Value} of a specific
 * property, typically of an {@link Algorithm}, such that changing this
 * {@link Value} would affect its behavior. As such, a {@link Parameter} allows
 * to retrieve its current {@link Value} through {@link #get()} and change it
 * through {@link #set(Object)}. The possible {@link Value}s are provided by
 * {@link #getSpace()}. Each {@link Parameter} is also identified through its
 * name ({@link #getName()}) and further detailed through its description (
 * {@link #getDescription()}).
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Value>
 *            the type of value the {@link Parameter} can manage
 */
public interface Parameter<Value> {
	/**
	 * 
	 * @param value
	 *            the {@link Value} to give to this {@link Parameter}
	 * @throws InvalidValueException
	 *             if the {@link Value} provided is not in the
	 *             {@link ParameterSpace} returned by {@link #getSpace()}
	 */
	public void set(Value value) throws InvalidValueException;

	/**
	 * 
	 * @return the current {@link Value} of the {@link Parameter}
	 */
	public Value get();

	/**
	 * 
	 * @return the {@link ParameterSpace} describing the space of values
	 *         admissible for {@link #set(Object)}
	 */
	public ParameterSpace<Value> getSpace();

	@SuppressWarnings("serial")
	public static class InvalidValueException extends RuntimeException {
		public <Value> InvalidValueException(Parameter<Value> parameter,
				Value value, String message) {
			super("Invalid value " + value + " for parameter " + parameter
					+ ": " + message);
		}

		public <Value> InvalidValueException(Parameter<Value> parameter,
				Value value) {
			super("Invalid value " + value + " for parameter " + parameter);
		}
	}
}
