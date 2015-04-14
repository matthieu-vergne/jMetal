package org.uma.jmetal.parameter.generator.representer.impl;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.uma.jmetal.parameter.generator.representer.ValueRepresenter;

public class ValueRepresenterFactory {

	/**
	 * This method provide a naive {@link ValueRepresenter} which maps
	 * {@link Number}s to their {@link String} representations. Basically, the
	 * {@link String} representation is provided by the
	 * {@link Object#toString()} method of the {@link Number}, and the
	 * {@link String} provided to retrieve the {@link Number} is parsed using
	 * the right parser, depending on the argument provided.<br/>
	 * <br/>
	 * NB: This argument is required because of the type erasure of Java, which
	 * makes us unable to retrieve the generic type requested.
	 * 
	 * @param numberType
	 *            the specific type of {@link Number} to represent
	 * @return a simple {@link ValueRepresenter} for {@link Number}s
	 */
	public <Value extends Number> ValueRepresenter<Value, String> createNumberStringRepresenter(
			final Class<Value> numberType) {
		return new ValueRepresenter<Value, String>() {

			@Override
			public String toRepresentation(Value value) {
				return value.toString();
			}

			@SuppressWarnings("unchecked")
			@Override
			public Value toValue(String representation) {
				if (numberType.equals(Integer.class)) {
					return (Value) (Integer) Integer.parseInt(representation);
				} else if (numberType.equals(Short.class)) {
					return (Value) (Short) Short.parseShort(representation);
				} else if (numberType.equals(Long.class)) {
					return (Value) (Long) Long.parseLong(representation);
				} else if (numberType.equals(Byte.class)) {
					return (Value) (Byte) Byte.parseByte(representation);
				} else if (numberType.equals(Float.class)) {
					return (Value) (Float) Float.parseFloat(representation);
				} else if (numberType.equals(Double.class)) {
					return (Value) (Double) Double.parseDouble(representation);
				} else if (numberType.equals(BigInteger.class)) {
					return (Value) BigInteger.valueOf(Long
							.parseLong(representation));
				} else if (numberType.equals(BigDecimal.class)) {
					return (Value) BigDecimal.valueOf(Double
							.parseDouble(representation));
				} else {
					throw new IllegalArgumentException("Cannot parse it as a "
							+ numberType + ": " + representation);
				}
			}
		};
	}

	/**
	 * The {@link ValueRepresenter} provided by this method is the most naive
	 * one for {@link String} values: it just return the {@link String} itself.
	 * This {@link ValueRepresenter} fits the cases where we want to represent a
	 * redundancy-free list (i.e. a set) of {@link String}s as-is.
	 * 
	 * @return a naive {@link ValueRepresenter} for {@link String}s
	 */
	public ValueRepresenter<String, String> createSimpleStringRepresenter() {
		return new ValueRepresenter<String, String>() {

			@Override
			public String toRepresentation(String value) {
				return value;
			}

			@Override
			public String toValue(String representation) {
				return representation;
			}
		};
	}

}
