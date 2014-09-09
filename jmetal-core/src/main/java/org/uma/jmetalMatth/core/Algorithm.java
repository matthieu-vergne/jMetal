package org.uma.jmetalMatth.core;

/**
 * An {@link Algorithm} aims at providing a way to generate {@link Solution}s to
 * solve {@link Problem}s. Depending on the process implemented by an
 * {@link Algorithm}, different {@link Result}s can be generated, such as a
 * unique {@link Solution} or a collection of several {@link Solution}s. Also,
 * as an {@link Algorithm} provides a specific process, it is designed to
 * consider specific data in these {@link Solution}s, such as a set of integers
 * or real numbers, or something more heterogeneous. A solution {@link Encoding}
 * is provided by the {@link Algorithm} to represent this data.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Encoding>
 *            The specific data processed by the {@link Algorithm}.
 * @param <Manager>
 *            A manager aiming at providing methods which are not provided at
 *            the {@link Solution} level.
 * @param <Result>
 *            The data provided by a single execution of this {@link Algorithm},
 *            typically a single {@link Solution} or a collection of
 *            {@link Solution}s.
 */
public interface Algorithm<Encoding, Manager extends SolutionManager<Encoding, Solution<?, Encoding>>, Result> {

	public Result execute(Manager manager);
}
