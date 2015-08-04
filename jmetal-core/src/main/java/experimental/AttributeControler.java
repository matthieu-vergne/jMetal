package experimental;

/**
 * An {@link AttributeControler} provides the facilities of both
 * {@link AttributeReader} and {@link AttributeWriter}. It is intended to manage
 * attributes which are controlled by external entities, thus giving access to a
 * storage where these entities can write to and read from.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Item>
 *            the type of {@link Item} we can manage
 * @param <Value>
 *            the type of the attribute
 */
public interface AttributeControler<Item, Value> extends
		AttributeReader<Item, Value>, AttributeWriter<Item, Value> {

}
