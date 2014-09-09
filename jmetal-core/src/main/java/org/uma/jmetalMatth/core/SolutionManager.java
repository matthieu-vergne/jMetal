package org.uma.jmetalMatth.core;

/**
 * A {@link SolutionManager} aims at providing methods to use on
 * {@link Solution}s broadly. Typically, a {@link Solution} is used as a data
 * structure while the {@link SolutionManager} is used for computation.
 * Otherwise, a {@link Solution} centralizes the methods which applies on a
 * single {@link Solution} while the {@link SolutionManager} provide the methods
 * which applies on more than one {@link Solution}.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Representation>
 *            The {@link Problem} representation of the {@link Solution}.
 * @param <Encoding>
 *            The {@link Algorithm} encoding of the {@link Solution}.
 * @param <S>
 *            The specific type of {@link Solution} managed by this
 *            {@link SolutionManager}.
 */
public interface SolutionManager<Encoding, S extends Solution<?, Encoding>> {

	/**
	 * 
	 * @param candidate1
	 *            a candidate solution
	 * @param candidate2
	 *            another candidate solution
	 * @return the best solution between the two candidates, <code>null</code>
	 *         if they are equivalent or cannot be compared
	 */
	// FIXME redundant with the problem method
	public S selectBest(S candidate1, S candidate2);

}
