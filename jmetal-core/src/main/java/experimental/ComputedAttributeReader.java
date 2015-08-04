package experimental;

import java.util.WeakHashMap;

/**
 * A {@link ComputedAttributeReader} is an {@link AttributeReader} dedicated to
 * computed values (which are not set manually). When
 * {@link #getAttribute(Object)} is called the first time on a given
 * {@link Item}, {@link #compute(Object)} is called on this {@link Item} to
 * compute its {@link Value} before to store it. The next time
 * {@link #getAttribute(Object)} is called on the same {@link Item}, the
 * {@link Value} is retrieved to not recompute it, unless {@link #clear(Object)}
 * or {@link #clearAll()} have been called. This allows to compute the attribute
 * {@link Value}s on demand.<br/>
 * <br/>
 * Notice that weak references are used to not keep the {@link Value}s in memory
 * if the {@link Item} is not there anymore. So it should not result in memory
 * leaks.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Item>
 * @param <Value>
 */
public abstract class ComputedAttributeReader<Item, Value> implements
		AttributeReader<Item, Value> {

	/**
	 * The cache where the values are stored.
	 */
	private final WeakHashMap<Item, Value> cache = new WeakHashMap<>();

	@Override
	public Value getAttribute(Item item) {
		Value value = cache.get(item);
		if (value == null) {
			value = compute(item);
			cache.put(item, value);
		} else {
			// keep current value
		}
		return value;
	}

	/**
	 * This method allows to forget about a given {@link Item}. The next time
	 * {@link #getAttribute(Object)} is called on this {@link Item}, it will not
	 * retrieve its {@link Value}, thus forcing the re-computation.
	 * 
	 * @param item
	 *            the {@link Item} to forget
	 */
	public void clear(Item item) {
		cache.remove(item);
	}

	/**
	 * This method is equivalent to calling {@link #clear(Object)} on all the
	 * {@link Item}s known so far.
	 */
	public void clearAll() {
		cache.clear();
	}

	/**
	 * 
	 * @param item
	 *            the {@link Item} to compute the attribute from
	 * @return the current {@link Value} of its attribute
	 */
	public abstract Value compute(Item item);

}
