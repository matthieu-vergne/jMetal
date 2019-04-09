package org.uma.jmetal.service.model.runnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.uma.jmetal.service.model.runnable.Run.Params;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ParamDefinition<T> {

	private final String name;
	private final ExposedType<T> type;
	private final List<Object> examples;

	private ParamDefinition(String name, ExposedType<T> type, List<Object> examples) {
		this.name = name;
		this.type = type;
		this.examples = examples;
	}

	@JsonIgnore
	public String getName() {
		return name;
	}

	public ExposedType<T> getType() {
		return type;
	}

	public T from(Params params) {
		Object value = params.get(name);
		if (value == null) {
			throw new MissingParamException(this);
		}
		return type.toInternType(value);
	}

	public List<Object> getExamples() {
		return examples;
	}

	public Object createExample() {
		return examples.isEmpty() ? null : examples.get(0);
	}

	public static <T> ParamDefinition<T> of(ExposedType<T> type, String name) {
		return new ParamDefinition<>(name, type, Arrays.asList());
	}

	public ParamDefinition<T> withExample(T example) {
		List<Object> examples = new ArrayList<>(this.examples);
		examples.add(type.toExposedType(example));
		return new ParamDefinition<>(name, type, examples);
	}

	@SuppressWarnings("serial")
	static class MissingParamException extends NoSuchElementException {
		public MissingParamException(ParamDefinition<?> param) {
			super("Missing parameter " + param.getName());
		}
	}

}
