package org.uma.jmetal.generator;

/**
 * A {@link Generator} allows to generate one or several {@link Value}s. For
 * instance, one can implement a random generator which generates any
 * {@link Value} from a valid set of {@link Value}s (a different one at each
 * call of {@link #generate()}) or a {@link Generator} which can be configured
 * to generate a specific value.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Value>
 */
public interface Generator<Value> {
	/**
	 * 
	 * @return the generated {@link Value}
	 */
	public Value generate();
}
