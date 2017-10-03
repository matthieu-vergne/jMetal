package org.uma.jmetal.util.pareto.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import org.uma.jmetal.util.pareto.ParetoComparator;

public class ComparatorsBasedParetoComparator<T> implements ParetoComparator<T> {

	private final Collection<Comparator<T>> comparators;

	public ComparatorsBasedParetoComparator(Collection<Comparator<T>> comparators) {
		this.comparators = Collections.unmodifiableCollection(new LinkedList<>(comparators));
	}

	@Override
	public ParetoOrder compare(T a, T b) {
		int superior = 0;
		int inferior = 0;
		for (Comparator<T> comparator : comparators) {
			int comparison = comparator.compare(a, b);
			if (comparison > 0) {
				superior++;
			} else if (comparison < 0) {
				inferior++;
			} else {
				// Equal case
			}
			if (superior > 0 && inferior > 0) {
				return ParetoOrder.UNDETERMINED;
			} else {
				continue;
			}
		}

		if (inferior > 0 && superior == 0) {
			return ParetoOrder.INFERIOR;
		} else if (inferior == 0 && superior > 0) {
			return ParetoOrder.SUPERIOR;
		} else {
			return ParetoOrder.EQUAL;
		}
	}

	@Override
	public int dimensions() {
		return comparators.size();
	}

	@Override
	public Iterator<Comparator<T>> iterator() {
		return comparators.iterator();
	}

}
