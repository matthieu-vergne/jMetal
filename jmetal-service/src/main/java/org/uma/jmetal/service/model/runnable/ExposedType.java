package org.uma.jmetal.service.model.runnable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.uma.jmetal.service.serial.SolutionSerializer;
import org.uma.jmetal.solution.Solution;

import com.fasterxml.jackson.annotation.JsonValue;

public class ExposedType<T> {

	public static final ExposedType<Void> VOID = fromClass("void", Void.class);
	public static final ExposedType<String> STRING = fromClass("string", String.class);
	public static final ExposedType<Double> DOUBLE = fromClass("double", Double.class);

	private final String name;
	private final Function<Object, T> internalizer;
	private final Function<T, Object> exposer;

	public ExposedType(String name, Function<Object, T> internalizer, Function<T, Object> exposer) {
		this.name = name;
		this.internalizer = internalizer;
		this.exposer = exposer;
	}

	@JsonValue
	public String getName() {
		return name;
	}

	public T toInternType(Object value) {
		return internalizer.apply(value);
	}

	public Object toExposedType(T value) {
		return exposer.apply(value);
	}

	@Override
	public String toString() {
		return name;
	}

	public static <T> ExposedType<T> fromClass(String name, Class<T> clazz) {
		return new ExposedType<>(name, clazz::cast, x -> x);
	}

	public static <T> ExposedType<Solution<T>> solutionVars(ExposedType<T> varType) {
		return new ExposedType<>("solution.variables[" + varType.name + "]", SolutionSerializer::fromVars,
				SolutionSerializer::toVars);
	}

	public static <T> ExposedType<List<T>> list(ExposedType<T> itemType) {
		@SuppressWarnings("unchecked")
		Function<Object, List<T>> internalizer = l -> ((List<T>) l).stream().map(itemType::toInternType)
				.collect(Collectors.toList());
		Function<List<T>, Object> exposer = l -> l.stream().map(itemType::toExposedType).collect(Collectors.toList());
		return new ExposedType<>("list[" + itemType.name + "]", internalizer, exposer);
	}
}
