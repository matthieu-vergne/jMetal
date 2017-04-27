package org.uma.jmetal.solution;

/**
 * A {@link Variable} represents the fundamental information of a set of
 * homogeneous {@link Solution}s (e.g. a population of solutions returned by an
 * algorithm). For instance, an algorithm used to solve a TSP problem would
 * manage a whole population of {@link Solution}s, each representing a different
 * path, and a {@link Variable} would represent a type of information which
 * defines these {@link Solution}s, like the path they represent or something
 * more fine grained like the i<sup>th</sup> city.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Solution>
 * @param <Value>
 */
public interface Variable<Solution, Value> {
	/**
	 * 
	 * @param solution
	 *            the {@link Solution} to read
	 * @return the {@link Value} of the {@link Variable} for this
	 *         {@link Solution}
	 */
	public Value readFrom(Solution solution);
}
