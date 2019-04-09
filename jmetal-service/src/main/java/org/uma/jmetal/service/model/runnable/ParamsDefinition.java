package org.uma.jmetal.service.model.runnable;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Rel;

@SuppressWarnings("serial")
public class ParamsDefinition extends LinkedHashMap<String, ParamDefinition<?>>
		implements Map<String, ParamDefinition<?>> {

	public ParamsDefinition(Collection<ParamDefinition<?>> params) {
		params.forEach(p -> put(p.getName(), p));
	}

	public ParamsDefinition(ParamDefinition<?>... params) {
		this(Arrays.<ParamDefinition<?>>asList(params));
	}

	public ParamsExample createExample() {
		return new ParamsExample(values().stream().collect(Collectors.toMap(p -> p.getName(), p -> p.createExample())));
	}

	public static class Response extends ResourceSupport {

		public final ParamsDefinition definition;

		public Response(ParamsDefinition def, ResourceSupport parent, String parentId, String parentRel) {
			this.definition = def;
			add(parent.getLink(Rel.PARAMS_DEFINITION).withRel(Rel.SELF));
			add(parent.getLink(Rel.SELF).withRel(parentRel));
			add(parent.getLink(Rel.PARAMS_EXAMPLE));
		}

	}

	public static ParamsDefinition empty() {
		return new ParamsDefinition();
	}

}
