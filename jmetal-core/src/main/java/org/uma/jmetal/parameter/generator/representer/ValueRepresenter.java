package org.uma.jmetal.parameter.generator.representer;

/**
 * A {@link ValueRepresenter} is a bijective function which links a space of
 * {@link Value}s to a space of {@link Representation}s. Usually, one can think
 * of the {@link Object#toString()} method which provides the {@link String}
 * representation of an instance, but a {@link ValueRepresenter} which provides
 * a {@link String} {@link Representation} for a given {@link Value} should
 * ensure that this {@link Representation} is provided <b>only</b> for this
 * {@link Value}. Moreover, it should allow to retrieve a {@link Value} if we
 * give it the corresponding {@link Representation}.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Value>
 * @param <Representation>
 */
public interface ValueRepresenter<Value, Representation> {

	/**
	 * 
	 * @param value
	 *            the {@link Value} to represent
	 * @return the {@link Representation} of this {@link Value}
	 */
	public Representation toRepresentation(Value value);

	/**
	 * 
	 * @param representation
	 *            the {@link Representation} to consider
	 * @return the {@link Value} corresponding to this {@link Representation}
	 */
	public Value toValue(Representation representation);
}
