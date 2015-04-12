package org.uma.jmetal.parameter.generator;

import org.uma.jmetal.parameter.Parameter;

/**
 * A {@link GenerableParameter} is a {@link Parameter} providing facilities to
 * generate its {@link Value}s. The {@link ValueGenerator} provided by
 * {@link #getValueGenerator()} allows to manage the creation of a valid
 * {@link Value} in a generic way, by mapping the custom generation to a generic
 * one like selecting an index within a list.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Value>
 */
public interface GenerableParameter<Value> extends Parameter<Value> {

	/**
	 * 
	 * @return a {@link ValueGenerator} able to build valid values for this
	 *         {@link Parameter}
	 */
	public ValueGenerator<Value> getValueGenerator();
}
