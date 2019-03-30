package org.uma.jmetal.service.model.runnable;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Link;

public class ResultDefinition extends ResourceSupport {

	public final String description;

	public ResultDefinition(ResourceSupport parent, String parentId, String parentRel) {
		this.description = "Here should appear some definitions of the result of " + parentId + ".";
		add(parent.getLink(Link.REL_RESULT_DEFINITION).withRel(Link.REL_SELF));
		add(parent.getLink(Link.REL_SELF).withRel(parentRel));
		add(parent.getLink(Link.REL_RESULT_EXAMPLE));
	}

}
