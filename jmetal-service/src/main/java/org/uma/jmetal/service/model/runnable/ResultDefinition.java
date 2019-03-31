package org.uma.jmetal.service.model.runnable;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Rel;

public class ResultDefinition {

	public static class Response extends ResourceSupport {

		public final String description;

		public Response(ResourceSupport parent, String parentId, String parentRel) {
			this.description = "Here should appear some definitions of the result of " + parentId + ".";
			add(parent.getLink(Rel.RESULT_DEFINITION).withRel(Rel.SELF));
			add(parent.getLink(Rel.SELF).withRel(parentRel));
			add(parent.getLink(Rel.RESULT_EXAMPLE));
		}

	}

}
