package org.uma.jmetal.service.model.runnable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Rel;

public class ParamsDefinition {

	private final Collection<ParamDefinition<?>> params;

	public ParamsDefinition(Collection<ParamDefinition<?>> params) {
		this.params = params;
	}

	public Collection<ParamDefinition<?>> getParams() {
		return params;
	}

	public ParamsExample createExample() {
		return new ParamsExample(params.stream().collect(Collectors.toMap(p -> p.getName(), p -> p.createExample())));
	}

	public static class Response extends ResourceSupport {

		public final Map<String, Object> params;

		public Response(ParamsDefinition definition, ResourceSupport parent, String parentId, String parentRel) {
			this.params = definition.getParams().stream().collect(Collectors.toMap(p -> p.getName(), p -> p));
			add(parent.getLink(Rel.PARAMS_DEFINITION).withRel(Rel.SELF));
			add(parent.getLink(Rel.SELF).withRel(parentRel));
			add(parent.getLink(Rel.PARAMS_EXAMPLE));
		}

	}

	public static ParamsDefinition empty() {
		return new ParamsDefinition(Arrays.asList());
	}

}
