package org.uma.jmetal.parameter.generator.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.uma.jmetal.parameter.generator.ValueGenerator;

/* 
 * TODO Instead of having readable/writable parameters accessed through their names
 * and dedicated methods, reuse the Parameter objects to have an homogeneous management.
 * It could be that generators needing specific settings should be Parameterable, while
 * non-Parameterable generators should be considered as automatic generators.
 */
public class BuilderBasedGenerator<Value> implements ValueGenerator<Value> {
	private final Map<String, Method> setters;
	private final Map<String, Method> getters;
	private final Method validator;
	private final Method generator;
	private final Method reset;
	private final Object builder;

	public BuilderBasedGenerator(Object builder, String validatorMethod,
			String generatorMethod, String resetMethod) {
		Class<? extends Object> builderClass = builder.getClass();
		validator = retrieveEmptyMethod(builderClass, validatorMethod);
		generator = retrieveEmptyMethod(builderClass, generatorMethod);
		reset = retrieveEmptyMethod(builderClass, resetMethod);

		this.builder = builder;
		setters = new HashMap<>();
		getters = new HashMap<>();
		for (Method method : builderClass.getMethods()) {
			String name = method.getName();
			Class<?>[] argumentTypes = method.getParameterTypes();
			Class<?> returnType = method.getReturnType();
			if (name.matches("^set[^a-z].*$") && argumentTypes.length == 1) {
				setters.put(name.substring(3), method);
			} else if (name.matches("^get[^a-z].*$")
					&& argumentTypes.length == 0 && returnType != null) {
				getters.put(name.substring(3), method);
			} else {
				// other kind of method, we don't use them
			}
		}

		setters.remove(validator);
		setters.remove(generator);
		setters.remove(reset);

		getters.remove(validator);
		getters.remove(generator);
		getters.remove(reset);
	}

	private Method retrieveEmptyMethod(Class<?> clazz, String targetName) {
		for (Method method : clazz.getMethods()) {
			String name = method.getName();
			Class<?>[] argumentTypes = method.getParameterTypes();
			if (name.equals(targetName) && argumentTypes.length == 0) {
				return method;
			} else {
				// other kind of method, we don't use them
			}
		}
		throw new RuntimeException("Method not found: " + clazz.getName() + "."
				+ targetName + "()");
	}

	public Collection<String> getWritableParameters() {
		return setters.keySet();
	}

	public Collection<String> getReadableParameters() {
		return getters.keySet();
	}

	public void setParameter(String parameter, Object value) {
		try {
			setters.get(parameter).invoke(builder, new Object[] { value });
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public Object getParameter(String parameter) {
		try {
			return getters.get(parameter).invoke(builder, new Object[0]);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean canGenerate() {
		try {
			return (Boolean) validator.invoke(builder, new Object[0]);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Value generate() {
		try {
			return (Value) generator.invoke(builder, new Object[0]);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
