package experimental;

/**
 * An {@link AttributeAccessor} provides an access to a given kind of
 * attributes. {@link AttributeReader} and {@link AttributeWriter} are
 * specialization which provide respectively the reading and writing accesses.
 * An {@link AttributeAccessor} by itself does not provide specific accesses but
 * provide a way to identify when one or more accesses are available.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Item>
 *            the type of {@link Item} we can access to
 * @param <Value>
 *            the type of the attribute
 */
public interface AttributeAccessor<Item, Value> {

}
