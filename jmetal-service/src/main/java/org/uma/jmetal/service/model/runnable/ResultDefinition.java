package org.uma.jmetal.service.model.runnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Rel;

public class ResultDefinition<T> {

	private final ExposedType<T> type;
	private final List<Object> examples;

	private ResultDefinition(ExposedType<T> type, List<Object> examples) {
		this.type = type;
		this.examples = examples;
	}

	public ExposedType<T> getType() {
		return type;
	}

	public List<Object> getExamples() {
		return examples;
	}

	public Object createExample() {
		return examples.isEmpty() ? null : examples.get(0);
	}

	public static <T> ResultDefinition<T> of(ExposedType<T> type) {
		return new ResultDefinition<>(type, Arrays.asList());
	}

	public ResultDefinition<T> withExample(T example) {
		List<Object> examples = new ArrayList<>(this.examples);
		examples.add(type.toExposedType(example));
		return new ResultDefinition<>(type, examples);
	}

	public static ResultDefinition<Void> empty() {
		return of(ExposedType.VOID);
	}

	public static class Response extends ResourceSupport {

		public final ResultDefinition<?> definition;

		public Response(ResultDefinition<?> def, ResourceSupport parent, String parentId, String parentRel) {
			this.definition = def;
			add(parent.getLink(Rel.RESULT_DEFINITION).withRel(Rel.SELF));
			add(parent.getLink(Rel.SELF).withRel(parentRel));
			add(parent.getLink(Rel.RESULT_EXAMPLE));
		}

	}

}
