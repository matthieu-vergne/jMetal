package org.uma.jmetal.solution;

/**
 * An {@link Objective} represents the evaluation information of a
 * {@link Solution} on a given dimension. For instance, an algorithm used to
 * solve a TSP problem would manage lists of cities, ordered in a specific path,
 * and an {@link Objective} could be the length of the path, the time needed to
 * travel through this path, or the amount of fuel consumed.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Solution>
 * @param <Value>
 */
public interface Objective<Solution, Value> {
	/**
	 * 
	 * @param solution
	 *            the {@link Solution} to read
	 * @return the {@link Value} of the {@link Objective} for this
	 *         {@link Solution}
	 */
	public Value readFrom(Solution solution);
}
