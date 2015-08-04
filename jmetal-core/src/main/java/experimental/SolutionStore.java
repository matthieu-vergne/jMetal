package experimental;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link SolutionStore} instance provides a facility to store all the data of
 * a given solution. Initially, any entity could rely on {@link AttributeReader}
 * s and {@link AttributeWriter}s to store and access specific attributes, but
 * having each of them managing its own storage could have performance issues if
 * it is not managed well for each of them. Such a management would lead to an
 * <i>attribute-based</i> storage, where the values of all the <s>solutions</s>
 * for a given <s>attribute</s> are managed together. By using this
 * {@link SolutionStore} we provide a <i>solution-based</i> storage, where the
 * values of all the <s>attributes</s> for a given <s>solution</s> are managed
 * together. Furthermore, one can then use {@link SolutionAttributeControler}s
 * to easily implement the {@link AttributeControler}s of the
 * {@link SolutionStore}s.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public class SolutionStore {

	/**
	 * The values stored by this {@link SolutionStore}, which represent all the
	 * data relating to the represented solution.
	 */
	private final Map<AttributeAccessor<SolutionStore, ?>, Object> values = new HashMap<AttributeAccessor<SolutionStore, ?>, Object>();

	/**
	 * 
	 * @param accessor
	 *            the {@link AttributeWriter} corresponding to the attribute to
	 *            write
	 * @param value
	 *            the new value of the attribute
	 */
	public <Value> void setAttribute(
			AttributeWriter<SolutionStore, Value> accessor, Value value) {
		values.put(accessor, value);
	}

	/**
	 * 
	 * @param accessor
	 *            the {@link AttributeReader} corresponding to the attribute to
	 *            read
	 * @return the value of the attribute
	 */
	@SuppressWarnings("unchecked")
	public <Value> Value getAttribute(
			AttributeReader<SolutionStore, Value> accessor) {
		return (Value) values.get(accessor);
	}

	/**
	 * The {@link SolutionAttributeControler} is an {@link AttributeControler}
	 * designed specifically for interacting with the data of
	 * {@link SolutionStore}s. While one can use his own implementation, for a
	 * basic read/write interaction he can simply instantiate a
	 * {@link SolutionAttributeControler}.
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 * 
	 * @param <Value>
	 *            the type of the attribute
	 */
	public static class SolutionAttributeControler<Value> implements
			AttributeControler<SolutionStore, Value> {

		@Override
		public void setAttribute(SolutionStore solution, Value value) {
			solution.setAttribute(this, value);
		}

		@Override
		public Value getAttribute(SolutionStore solution) {
			return solution.getAttribute(this);
		}

	}
}
