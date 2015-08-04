package experimental;

/**
 * An {@link AttributeReader} aims at giving a reading access to a specific
 * attribute of an {@link Item}, independently of how this attribute is stored.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Item>
 *            the type of {@link Item} we can read the attribute from
 * @param <Value>
 *            the type of the attribute
 */
public interface AttributeReader<Item, Value> extends AttributeAccessor<Item, Value> {

	/**
	 * 
	 * @param item
	 *            the {@link Item} to read the attribute from
	 * @return the {@link Value} of the attribute of the specified {@link Item}
	 * @throws IllegalArgumentException
	 *             if the {@link Item} does not have such attribute
	 */
	public Value getAttribute(Item item) throws IllegalArgumentException;
}
