package experimental;

/**
 * An {@link AttributeWriter} aims at giving a writing access to a specific
 * attribute of an {@link Item}, independently of how this attribute is stored.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Item>
 *            the type of {@link Item} we can write the attribute to
 * @param <Value>
 *            the type of the attribute
 */
public interface AttributeWriter<Item, Value> extends AttributeAccessor<Item, Value> {

	/**
	 * 
	 * @param item
	 *            the {@link Item} to write the attribute to
	 * @param value
	 *            the new {@link Value} of the attribute
	 * @throws IllegalArgumentException
	 *             if the {@link Item} does not have such attribute or if the
	 *             {@link Value} is invalid
	 */
	public void setAttribute(Item item, Value value)
			throws IllegalArgumentException;
}
