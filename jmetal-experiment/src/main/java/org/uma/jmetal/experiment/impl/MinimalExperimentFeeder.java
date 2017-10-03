package org.uma.jmetal.experiment.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.uma.jmetal.experiment.FECExperiment;
import org.uma.jmetal.experiment.ExperimentFeeder;

/**
 * A {@link MinimalExperimentFeeder} provides a simple way to store algorithm
 * instances into an {@link ExperimentFeeder} for reuse in an
 * {@link FECExperiment}. Algorithm instances can be provided directly to the
 * constructor {@link #SimpleExperimentFeeder(Iterable)} or on a dynamic basis
 * through {@link #add(Object)} and {@link #addAll(Iterable)}. Notice that no
 * unicity check is made by this {@link MinimalExperimentFeeder}: if an instance
 * is added several times, it will be provided several times by the feeder.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 *
 * @param <Algorithm>
 */
public class MinimalExperimentFeeder<Algorithm> implements ExperimentFeeder<Algorithm> {

	private final List<Algorithm> algorithms = new LinkedList<>();

	/**
	 * Instantiate a {@link MinimalExperimentFeeder} with some algorithms.
	 * 
	 * @param algorithms
	 *            the algorithms to start with
	 */
	public MinimalExperimentFeeder(Iterable<Algorithm> algorithms) {
		addAll(algorithms);
	}

	/**
	 * Instantiate an empty {@link MinimalExperimentFeeder}.
	 */
	public MinimalExperimentFeeder() {
		// Nothing to do
	}

	/**
	 * Add another algorithm to this {@link MinimalExperimentFeeder}.
	 * 
	 * @param algorithm
	 *            the algorithm to add
	 */
	public void add(Algorithm algorithm) {
		algorithms.add(algorithm);
	}

	/**
	 * Add other algorithms to this {@link MinimalExperimentFeeder}.
	 * 
	 * @param algorithms
	 *            the algorithms to add
	 */
	public void addAll(Iterable<Algorithm> algorithms) {
		for (Algorithm algorithm : algorithms) {
			add(algorithm);
		}
	}

	@Override
	public Iterator<Algorithm> iterator() {
		return algorithms.iterator();
	}

}
