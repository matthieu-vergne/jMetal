package org.uma.jmetal.util.pareto;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

import org.junit.Test;
import org.uma.jmetal.util.pareto.ParetoComparator.ParetoOrder;

public abstract class ParetoComparatorTest<T> {

	public static class Couple<T> {
		T item1;
		T item2;

		public Couple(T item1, T item2) {
			this.item1 = item1;
			this.item2 = item2;
		}

		@Override
		public String toString() {
			return "(" + item1 + ", " + item2 + ")";
		}
	}

	protected abstract ParetoComparator<T> generateParetoComparator();

	protected abstract Couple<T> generateCoupleToObtain(ParetoOrder order);

	@Test
	public void testParetoComparatorIsNotNull() {
		ParetoComparator<T> comparator = generateParetoComparator();
		assertNotNull(comparator);
	}

	@Test
	public void testParetoComparatorHasAtLeastOneDimension() {
		ParetoComparator<T> comparator = generateParetoComparator();
		assertTrue(comparator.dimensions() > 0);
	}

	@Test
	public void testParetoComparatorHasAsMuchComparatorsThanDimensions() {
		ParetoComparator<T> comparator = generateParetoComparator();
		Collection<Comparator<T>> subcomparators = new LinkedList<>();
		for (Comparator<T> subcomparator : comparator) {
			subcomparators.add(subcomparator);
		}
		assertEquals(comparator.dimensions(), subcomparators.size());
	}

	@Test
	public void testGeneratingInferiorCoupleLeadsToInferiorOrEqualDimensions() {
		ParetoComparator<T> comparator = generateParetoComparator();

		Couple<T> couple = generateCoupleToObtain(ParetoOrder.INFERIOR);
		int inferior = 0;
		for (Comparator<T> subcomparator : comparator) {
			int subcomparison = subcomparator.compare(couple.item1, couple.item2);
			if (subcomparison > 0) {
				fail("One dimension returns superior (" + subcomparison + "): " + subcomparator);
			} else if (subcomparison == 0) {
				// Equal case, ignore
			} else {
				inferior++;
			}
		}
		assertTrue("No dimension returns inferior", inferior > 0);
	}

	@Test
	public void testGeneratingSuperiorCoupleLeadsToSuperiorOrEqualDimensions() {
		ParetoComparator<T> comparator = generateParetoComparator();

		Couple<T> couple = generateCoupleToObtain(ParetoOrder.SUPERIOR);
		int superior = 0;
		for (Comparator<T> subcomparator : comparator) {
			int subcomparison = subcomparator.compare(couple.item1, couple.item2);
			if (subcomparison < 0) {
				fail("One dimension returns inferior (" + subcomparison + "): " + subcomparator);
			} else if (subcomparison == 0) {
				// Equal case, ignore
			} else {
				superior++;
			}
		}
		assertTrue("No dimension returns superior", superior > 0);
	}

	@Test
	public void testGeneratingEqualCoupleLeadsToEqualDimensions() {
		ParetoComparator<T> comparator = generateParetoComparator();

		Couple<T> couple = generateCoupleToObtain(ParetoOrder.EQUAL);
		for (Comparator<T> subcomparator : comparator) {
			int subcomparison = subcomparator.compare(couple.item1, couple.item2);
			if (subcomparison < 0) {
				fail("One dimension returns inferior (" + subcomparison + "): " + subcomparator);
			} else if (subcomparison == 0) {
				// Equal case, ignore
			} else {
				fail("One dimension returns superior (" + subcomparison + "): " + subcomparator);
			}
		}
		// Only equal cases, OK
	}

	@Test
	public void testGeneratingUndeterminedCoupleLeadsToInferiorAndSuperiorDimensions() {
		ParetoComparator<T> comparator = generateParetoComparator();

		Couple<T> couple = generateCoupleToObtain(ParetoOrder.UNDETERMINED);
		int inferior = 0;
		int superior = 0;
		for (Comparator<T> subcomparator : comparator) {
			int subcomparison = subcomparator.compare(couple.item1, couple.item2);
			if (subcomparison < 0) {
				inferior++;
			} else if (subcomparison == 0) {
				// Equal case, ignore
			} else {
				superior++;
			}
		}
		assertTrue("No dimension returns inferior", inferior > 0);
		assertTrue("No dimension returns superior", superior > 0);
	}

	@Test
	public void testParetoComparatorReturnsCorrectResultsOnInferiorItems() {
		ParetoComparator<T> comparator = generateParetoComparator();
		Couple<T> couple = generateCoupleToObtain(ParetoOrder.INFERIOR);
		assertEquals(couple + " should lead to " + ParetoOrder.INFERIOR, ParetoOrder.INFERIOR,
				comparator.compare(couple.item1, couple.item2));
		assertEquals(couple + " reversed should lead to " + ParetoOrder.SUPERIOR, ParetoOrder.SUPERIOR,
				comparator.compare(couple.item2, couple.item1));
	}

	@Test
	public void testParetoComparatorReturnsCorrectResultsOnSuperiorItems() {
		ParetoComparator<T> comparator = generateParetoComparator();
		Couple<T> couple = generateCoupleToObtain(ParetoOrder.SUPERIOR);
		assertEquals(couple + " should lead to " + ParetoOrder.SUPERIOR, ParetoOrder.SUPERIOR,
				comparator.compare(couple.item1, couple.item2));
		assertEquals(couple + " reversed should lead to " + ParetoOrder.INFERIOR, ParetoOrder.INFERIOR,
				comparator.compare(couple.item2, couple.item1));
	}

	@Test
	public void testParetoComparatorReturnsCorrectResultsOnEqualItems() {
		ParetoComparator<T> comparator = generateParetoComparator();
		Couple<T> couple = generateCoupleToObtain(ParetoOrder.EQUAL);
		assertEquals(couple + " should lead to " + ParetoOrder.EQUAL, ParetoOrder.EQUAL,
				comparator.compare(couple.item1, couple.item2));
		assertEquals(couple + " reversed should lead to " + ParetoOrder.EQUAL, ParetoOrder.EQUAL,
				comparator.compare(couple.item2, couple.item1));
	}

	@Test
	public void testParetoComparatorReturnsCorrectResultsOnUndeterminedItems() {
		ParetoComparator<T> comparator = generateParetoComparator();
		Couple<T> couple = generateCoupleToObtain(ParetoOrder.UNDETERMINED);
		assertEquals(couple + " should lead to " + ParetoOrder.UNDETERMINED, ParetoOrder.UNDETERMINED,
				comparator.compare(couple.item1, couple.item2));
		assertEquals(couple + " reversed should lead to " + ParetoOrder.UNDETERMINED, ParetoOrder.UNDETERMINED,
				comparator.compare(couple.item2, couple.item1));
	}

	@Test
	public void testConversionToStandardComparatorReturnsCorrectResultsOnInferiorItems() {
		Comparator<T> comparator = generateParetoComparator().toStandardComparator();
		Couple<T> couple = generateCoupleToObtain(ParetoOrder.INFERIOR);
		assertTrue(couple + " should be inferior", comparator.compare(couple.item1, couple.item2) < 0);
		assertTrue(couple + " reversed should be superior", comparator.compare(couple.item2, couple.item1) > 0);
	}

	@Test
	public void testConversionToStandardComparatorReturnsCorrectResultsOnSuperiorItems() {
		Comparator<T> comparator = generateParetoComparator().toStandardComparator();
		Couple<T> couple = generateCoupleToObtain(ParetoOrder.SUPERIOR);
		assertTrue(couple + " should be superior", comparator.compare(couple.item1, couple.item2) > 0);
		assertTrue(couple + " reversed should be inferior", comparator.compare(couple.item2, couple.item1) < 0);
	}

	@Test
	public void testConversionToStandardComparatorReturnsCorrectResultsOnEqualItems() {
		Comparator<T> comparator = generateParetoComparator().toStandardComparator();
		Couple<T> couple = generateCoupleToObtain(ParetoOrder.EQUAL);
		assertTrue(couple + " should be equal", comparator.compare(couple.item1, couple.item2) == 0);
		assertTrue(couple + " reversed should be equal", comparator.compare(couple.item2, couple.item1) == 0);
	}

	@Test
	public void testConversionToStandardComparatorReturnsCorrectResultsOnUndeterminedItems() {
		Comparator<T> comparator = generateParetoComparator().toStandardComparator();
		Couple<T> couple = generateCoupleToObtain(ParetoOrder.UNDETERMINED);
		assertTrue(couple + " should be equal", comparator.compare(couple.item1, couple.item2) == 0);
		assertTrue(couple + " reversed should be equal", comparator.compare(couple.item2, couple.item1) == 0);
	}
}
