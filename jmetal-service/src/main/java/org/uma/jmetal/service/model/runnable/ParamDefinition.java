package org.uma.jmetal.service.model.runnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.uma.jmetal.service.model.runnable.Run.Params;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ParamDefinition<T> {

	private final String exposedName;
	private final String type;
	private final Class<?> clazz;
	private final List<T> examples;

	private ParamDefinition(String exposedName, String type, Class<?> clazz, List<T> examples) {
		this.exposedName = exposedName;
		this.type = type;
		this.clazz = clazz;
		this.examples = examples;
	}

	@JsonIgnore
	public String getName() {
		return exposedName;
	}

	public String getType() {
		return type;
	}

	@SuppressWarnings("unchecked")
	public T getValue(Params params) {
		Object value = params.get(exposedName);
		if (value == null) {
			throw new MissingParamException(this);
		}
		return (T) clazz.cast(value);
	}

	public List<T> getExamples() {
		return examples;
	}

	public T createExample() {
		if (examples.isEmpty()) {
			return null;
		} else {
			return examples.get(0);
		}
	}

	public static ParamDefinition<String> string(String name) {
		return new ParamDefinition<>(name, "string", String.class, Arrays.asList());
	}

	public ParamDefinition<T> withExample(T example) {
		List<T> examples = new ArrayList<>(this.examples);
		examples.add(example);
		return new ParamDefinition<>(exposedName, type, clazz, examples);
	}

	@SuppressWarnings("serial")
	static class MissingParamException extends NoSuchElementException {
		public MissingParamException(ParamDefinition<?> param) {
			super("Missing parameter " + param.getName());
		}
	}

}
