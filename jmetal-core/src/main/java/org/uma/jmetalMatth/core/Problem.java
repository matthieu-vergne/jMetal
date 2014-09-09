package org.uma.jmetalMatth.core;

/**
 * A {@link Problem} aims at representing a context in which a set of candidate
 * {@link Solution}s can be evaluated. In particular, a {@link Problem} provides
 * a solution {@link Representation} depending on this context.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Representation>
 *            The representation of the {@link Solution}s considered by this
 *            {@link Problem}.
 */
public interface Problem<Representation> {

	/**
	 * 
	 * @param candidate1
	 *            a candidate solution
	 * @param candidate2
	 *            another candidate solution
	 * @return the best solution between the two candidates, <code>null</code>
	 *         if they are equivalent or cannot be compared
	 */
	public Solution<Representation, ?> selectBest(
			Solution<Representation, ?> candidate1,
			Solution<Representation, ?> candidate2);
}
