package org.uma.jmetalMatth.core;

/**
 * A {@link Solution} is an atomic element provided generally by an
 * {@link Algorithm} to solve a {@link Problem}. It can be composed of anything
 * but should be at least manipulable by the {@link Algorithm} and
 * {@link Problem} it is related to. Thus, it should specify a specific
 * {@link Representation} provided by a specific {@link Problem} and a specific
 * {@link Encoding} provided by a specific {@link Algorithm}.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Representation>
 *            The data processed by the {@link Problem} considering this
 *            {@link Solution}.
 * @param <Encoding>
 *            The data processed by the {@link Algorithm} managing this
 *            {@link Solution}.
 */
public interface Solution<Representation, Encoding> {

	/**
	 * 
	 * @return the specific {@link Representation} corresponding to the
	 *         {@link Problem} to solve.
	 */
	public Representation getRepresentation();

	/**
	 * 
	 * @return the specific {@link Encoding} corresponding to the
	 *         {@link Algorithm} which process this {@link Solution}.
	 */
	public Encoding getEncoding();
}
