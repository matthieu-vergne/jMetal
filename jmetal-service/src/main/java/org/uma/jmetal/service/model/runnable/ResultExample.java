package org.uma.jmetal.service.model.runnable;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Link;

public class ResultExample extends ResourceSupport {

	public final String description;

	public ResultExample(ResourceSupport parent, String parentId, String parentRel) {
		this.description = "Here should appear an example of result for " + parentId + ", based on its definition.";
		add(parent.getLink(Link.REL_RESULT_EXAMPLE).withRel(Link.REL_SELF));
		add(parent.getLink(Link.REL_SELF).withRel(parentRel));
		add(parent.getLink(Link.REL_RESULT_DEFINITION));
	}

}
