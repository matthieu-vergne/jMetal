package org.uma.jmetal.service.model.runnable;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Rel;

@SuppressWarnings("serial")
public class ParamsExample extends LinkedHashMap<String, Object> implements Map<String, Object> {

	public ParamsExample(Map<String, Object> params) {
		super(params);
	}

	public static class Response extends ResourceSupport {

		public final ParamsExample example;

		public Response(ParamsExample example, ResourceSupport parent, String parentId, String parentRel) {
			this.example = example;
			add(parent.getLink(Rel.PARAMS_EXAMPLE).withRel(Rel.SELF));
			add(parent.getLink(Rel.SELF).withRel(parentRel));
			add(parent.getLink(Rel.PARAMS_DEFINITION));
		}

	}

}
