package org.uma.jmetal.service.model.runnable;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Rel;

public class ResultExample {

	public static class Response extends ResourceSupport {

		public final Object example;

		public Response(Object example, ResourceSupport parent, String parentId, String parentRel) {
			this.example = example;
			add(parent.getLink(Rel.RESULT_EXAMPLE).withRel(Rel.SELF));
			add(parent.getLink(Rel.SELF).withRel(parentRel));
			add(parent.getLink(Rel.RESULT_DEFINITION));
		}

	}

}
