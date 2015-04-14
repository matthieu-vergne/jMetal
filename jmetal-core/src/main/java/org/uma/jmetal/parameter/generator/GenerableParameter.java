package org.uma.jmetal.parameter.generator;

import org.uma.jmetal.parameter.Parameter;
import org.uma.jmetal.parameter.generator.representer.ValueRepresenter;

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

	/**
	 * 
	 * @param representationClass
	 *            the type of data to use to represent the {@link Value}s
	 * @return <code>null</code> if no {@link ValueRepresenter} is available for
	 *         the requested representation, a valid {@link ValueRepresenter}
	 *         otherwise
	 */
	public <Representation> ValueRepresenter<Value, Representation> getRepresenterFor(
			Class<Representation> representationClass);
}
