package org.uma.jmetal.util.pareto;

import java.util.Comparator;
import java.util.Iterator;

/**
 * A {@link ParetoComparator} allows to compare two items by assigning them a
 * {@link ParetoOrder}.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 *
 * @param <T>
 */
// TODO Link to Wikipedia description
// TODO Reuse Wikipedia vocabulary
public interface ParetoComparator<T> extends Iterable<Comparator<T>> {
	/**
	 * A {@link ParetoOrder} tells how two items relate to each other by comparing
	 * them on various dimensions.
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 *
	 */
	public static enum ParetoOrder {
		/**
		 * The first item is strictly superior on one dimension and at least equal on
		 * all other dimensions.
		 */
		SUPERIOR,
		/**
		 * The first item is strictly inferior on one dimension and at most equal on all
		 * other dimensions.
		 */
		INFERIOR,
		/**
		 * The first item is strictly equal on all dimensions.
		 */
		EQUAL,
		/**
		 * The first item is strictly superior on at least one dimension and strictly
		 * inferior on at least one dimension.
		 */
		UNDETERMINED
	}

	/**
	 * Compare the two items by identifying their {@link ParetoOrder}.
	 * 
	 * @param a
	 *            the first item to compare
	 * @param b
	 *            the second item to compare
	 * @return the {@link ParetoOrder} relating the two items
	 */
	public ParetoOrder compare(T a, T b);

	/**
	 * 
	 * @return the number of dimensions of this {@link ParetoComparator}
	 */
	public int dimensions();

	/**
	 * Because a {@link ParetoComparator} builds a {@link ParetoOrder} based on
	 * various dimensions, a {@link Comparator} can be assigned to each of these
	 * dimensions. This method gives access to them.
	 * 
	 * @return an {@link Iterator} providing a {@link Comparator} for each dimension
	 *         of this {@link ParetoComparator}
	 */
	@Override
	public Iterator<Comparator<T>> iterator();

	/**
	 * As opposed to a standard {@link Comparator}, a {@link ParetoComparator} can
	 * return {@link ParetoOrder#UNDETERMINED}. This case shares with
	 * {@link ParetoOrder#EQUAL} the inability to tell which item is superior to the
	 * other. Consequently, this method creates a standard {@link Comparator} by
	 * reducing both these cases to a standard equality (0), while
	 * {@link ParetoOrder#SUPERIOR} is mapped to a standard superiority (1) and
	 * {@link ParetoOrder#INFERIOR} to a standard inferiority (-1).
	 * 
	 * @return a {@link Comparator} based on this {@link ParetoComparator}
	 */
	public default Comparator<T> toStandardComparator() {
		return new Comparator<T>() {

			@Override
			public int compare(T a, T b) {
				ParetoOrder order = ParetoComparator.this.compare(a, b);
				switch (order) {
				case INFERIOR:
					return -1;
				case SUPERIOR:
					return 1;
				case EQUAL:
				case UNDETERMINED:
					return 0;
				default:
					throw new RuntimeException("Not managed case: " + order);
				}
			}
		};
	}
}
