package org.uma.jmetal.parameter.generator.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class ValueGeneratorFactoryTest {

	@Test
	public void testIntegerRangeGeneratorGenerateProperIndexes() {
		ValueGeneratorFactory factory = new ValueGeneratorFactory();
		FiniteDiscreteRangeGenerator<Integer> generator = factory
				.createIntegerRangeGenerator(5, 10);

		assertEquals(5, generator.getMaxIndex());

		generator.setIndex(0);
		assertTrue(generator.canGenerate());
		assertEquals((Integer) 5, generator.generate());

		generator.setIndex(1);
		assertTrue(generator.canGenerate());
		assertEquals((Integer) 6, generator.generate());

		generator.setIndex(2);
		assertTrue(generator.canGenerate());
		assertEquals((Integer) 7, generator.generate());

		generator.setIndex(3);
		assertTrue(generator.canGenerate());
		assertEquals((Integer) 8, generator.generate());

		generator.setIndex(4);
		assertTrue(generator.canGenerate());
		assertEquals((Integer) 9, generator.generate());

		generator.setIndex(5);
		assertTrue(generator.canGenerate());
		assertEquals((Integer) 10, generator.generate());
	}

	@Test
	public void testIntegerRangeGeneratorDoNotGenerateImproperIndexes() {
		ValueGeneratorFactory factory = new ValueGeneratorFactory();
		FiniteDiscreteRangeGenerator<Integer> generator = factory
				.createIntegerRangeGenerator(5, 10);

		boolean exceptionThrown = false;
		try {
			generator.setIndex(-1);
		} catch (Exception e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown || !generator.canGenerate());

		exceptionThrown = false;
		try {
			generator.setIndex(generator.getMaxIndex() + 1);
		} catch (Exception e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown || !generator.canGenerate());
	}

	@Test
	public void testIntegerRangeGeneratorManageSingleValues() {
		ValueGeneratorFactory factory = new ValueGeneratorFactory();
		FiniteDiscreteRangeGenerator<Integer> generator = factory
				.createIntegerRangeGenerator(5, 5);

		assertEquals(0, generator.getMaxIndex());
		assertTrue(generator.canGenerate());
		assertEquals((Integer) 0, generator.getIndex());
		assertEquals((Integer) 5, generator.generate());
	}
	
	@Test
	public void testDoubleRangeGeneratorGenerateProperIndexes() {
		ValueGeneratorFactory factory = new ValueGeneratorFactory();
		FiniteContinuousRangeGenerator<Double> generator = factory
				.createDoubleRangeGenerator(5, 10);

		generator.setRatio(0.0);
		assertTrue(generator.canGenerate());
		assertEquals(5, generator.generate(), 0);

		generator.setRatio(1.0);
		assertTrue(generator.canGenerate());
		assertEquals(10, generator.generate(), 0);

		generator.setRatio(0.5);
		assertTrue(generator.canGenerate());
		assertEquals(7.5, generator.generate(), 0);
	}

	@Test
	public void testDoubleRangeGeneratorDoNotGenerateImproperIndexes() {
		ValueGeneratorFactory factory = new ValueGeneratorFactory();
		FiniteContinuousRangeGenerator<Double> generator = factory
				.createDoubleRangeGenerator(5, 10);

		boolean exceptionThrown = false;
		try {
			generator.setRatio(-0.1);
		} catch (Exception e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown || !generator.canGenerate());

		exceptionThrown = false;
		try {
			generator.setRatio(1.1);
		} catch (Exception e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown || !generator.canGenerate());
	}

	@Test
	public void testDoubleRangeGeneratorManageSingleValues() {
		ValueGeneratorFactory factory = new ValueGeneratorFactory();
		FiniteContinuousRangeGenerator<Double> generator = factory
				.createDoubleRangeGenerator(5, 5);

		assertTrue(generator.canGenerate());
		assertEquals(5, generator.generate(), 0);
		
		generator.setRatio(0.0);
		assertEquals(5, generator.generate(), 0);
		generator.setRatio(0.5);
		assertEquals(5, generator.generate(), 0);
		generator.setRatio(1.0);
		assertEquals(5, generator.generate(), 0);
	}
}
