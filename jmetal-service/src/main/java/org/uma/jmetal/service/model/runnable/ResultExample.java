package org.uma.jmetal.service.model.runnable;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Rel;

public class ResultExample extends ResourceSupport {

	public final String description;

	public ResultExample(ResourceSupport parent, String parentId, String parentRel) {
		this.description = "Here should appear an example of result for " + parentId + ", based on its definition.";
		add(parent.getLink(Rel.RESULT_EXAMPLE).withRel(Rel.SELF));
		add(parent.getLink(Rel.SELF).withRel(parentRel));
		add(parent.getLink(Rel.RESULT_DEFINITION));
	}

}
