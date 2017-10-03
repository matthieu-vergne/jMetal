package org.uma.jmetal.experiment.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.uma.jmetal.experiment.ExperimentFeeder;
import org.uma.jmetal.experiment.ExperimentFeederTest;

public class MinimalExperimentFeederTest extends ExperimentFeederTest {

	@Override
	protected <Algorithm> ExperimentFeeder<Algorithm> generateExperimentFeeder(Collection<Algorithm> algorithms) {
		return new MinimalExperimentFeeder<Algorithm>(algorithms);
	}

	@Test
	public void testAddProperlyAddsAlgorithm() {
		MinimalExperimentFeeder<Object> feeder = new MinimalExperimentFeeder<Object>();

		Object algo0 = new Object();
		feeder.add(algo0);
		Object algo1 = new Object();
		feeder.add(algo1);
		Object algo2 = new Object();
		feeder.add(algo2);

		Iterator<Object> iterator = feeder.iterator();
		assertEquals(algo0, iterator.next());
		assertEquals(algo1, iterator.next());
		assertEquals(algo2, iterator.next());
	}

	@Test
	public void testAddAllProperlyAddsAlgorithms() {
		MinimalExperimentFeeder<Object> feeder = new MinimalExperimentFeeder<Object>();

		Object algo0 = new Object();
		Object algo1 = new Object();
		Object algo2 = new Object();

		feeder.addAll(Arrays.asList(algo0, algo1, algo2));
		Iterator<Object> iterator = feeder.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(algo0, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(algo1, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(algo2, iterator.next());
		assertFalse(iterator.hasNext());
	}

}
