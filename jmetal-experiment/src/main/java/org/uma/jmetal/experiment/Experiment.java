package org.uma.jmetal.experiment;

/**
 * <p>
 * An {@link Experiment} allows to evaluate the performance of algorithms on
 * various problems. It can be useful when we design a new algorithm and we want
 * to compare it with others, especially to establish the state-of-the-art, or
 * when we design a component (e.g., an operator) and we want to compare a
 * number of versions of a same algorithms with different component variants.
 * </p>
 * </p>
 * Because an {@link Experiment} can have many purposes, its only requirement is
 * to be a procedure to execute, and thus a {@link Runnable}. Specific kinds of
 * experiments should extend this interface to describe its different steps and
 * how they are executed.
 * </p>
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 *
 */
public interface Experiment extends Runnable {
	/**
	 * Execute the {@link Experiment}.
	 */
	public void run();
}
