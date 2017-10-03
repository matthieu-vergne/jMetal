package org.uma.jmetal.experiment;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Test;

public abstract class ExperimentFeederTest {

	protected abstract <Algo> ExperimentFeeder<Algo> generateExperimentFeeder(Collection<Algo> algos);

	@Test
	public void testFeederHasCorrectSize() {
		Collection<Object> algorithms = new LinkedList<>();
		algorithms.add(new Object());
		algorithms.add(new Object());
		algorithms.add(new Object());
		algorithms.add(new Object());
		algorithms.add(new Object());
		ExperimentFeeder<Object> feeder = generateExperimentFeeder(algorithms);

		Iterator<Object> iterator = feeder.iterator();
		assertTrue(iterator.hasNext());
		iterator.next();
		assertTrue(iterator.hasNext());
		iterator.next();
		assertTrue(iterator.hasNext());
		iterator.next();
		assertTrue(iterator.hasNext());
		iterator.next();
		assertTrue(iterator.hasNext());
		iterator.next();
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testIteratorProvideCorrectAlgorithms() {
		Collection<Runnable> algorithms = new LinkedList<>();
		int count = 3;
		Boolean[] isRun = new Boolean[count];
		for (int i = 0; i < count; i++) {
			int index = i;
			isRun[index] = false;
			algorithms.add(new Runnable() {

				@Override
				public void run() {
					isRun[index] = true;
				}
			});
		}
		ExperimentFeeder<Runnable> feeder = generateExperimentFeeder(algorithms);

		Iterator<Runnable> iterator = feeder.iterator();
		for (int i = 0; i < count; i++) {
			assertTrue("Missing algo " + i, iterator.hasNext());
			Runnable algorithm = iterator.next();
			assertNotNull("Null algo " + i, algorithm);
			algorithm.run();
			long activated = Arrays.stream(isRun).filter((x) -> x).count();
			assertEquals(i + 1, activated);
		}
		assertFalse("Too much algos", iterator.hasNext());
	}
}
