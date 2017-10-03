package org.uma.jmetal.util.pareto.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

import org.uma.jmetal.util.pareto.ParetoComparator;
import org.uma.jmetal.util.pareto.ParetoComparator.ParetoOrder;
import org.uma.jmetal.util.pareto.ParetoComparatorTest;
import org.uma.jmetal.util.pareto.impl.ComparatorsBasedParetoComparatorTest.Item;

public class ComparatorsBasedParetoComparatorTest extends ParetoComparatorTest<Item> {

	public static class Item {
		int dim1;
		int dim2;

		public Item(int dim1, int dim2) {
			this.dim1 = dim1;
			this.dim2 = dim2;
		}

		@Override
		public String toString() {
			return "{" + dim1 + ", " + dim2 + "}";
		}
	}

	@Override
	protected ParetoComparator<Item> generateParetoComparator() {
		Collection<Comparator<Item>> comparators = new LinkedList<>();
		comparators.add((a, b) -> Integer.compare(a.dim1, b.dim1));
		comparators.add((a, b) -> Integer.compare(a.dim2, b.dim2));
		return new ComparatorsBasedParetoComparator<>(comparators);
	}

	@Override
	protected Couple<Item> generateCoupleToObtain(ParetoOrder order) {
		switch (order) {
		case EQUAL:
			return new Couple<>(new Item(3, 5), new Item(3, 5));
		case INFERIOR:
			return new Couple<>(new Item(3, 5), new Item(30, 50));
		case SUPERIOR:
			return new Couple<>(new Item(30, 50), new Item(3, 5));
		case UNDETERMINED:
			return new Couple<>(new Item(3, 5), new Item(5, 3));
		default:
			throw new RuntimeException("Unmanaged order: " + order);
		}
	}

}
