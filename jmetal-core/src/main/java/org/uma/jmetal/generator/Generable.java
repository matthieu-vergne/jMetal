package org.uma.jmetal.generator;

import java.util.Collection;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.Operator;

/**
 * A {@link Generable} refers to containers for which we can generate the
 * {@link Value}s. This can be applied for instance to parameters of
 * {@link Algorithm}s or {@link Operator}s.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Value>
 *            the type of value generated
 */
public interface Generable<Value> {

	/**
	 * 
	 * @return the supported {@link Generators}
	 */
	public Collection<Generator<Value>> getGenerators();
}
