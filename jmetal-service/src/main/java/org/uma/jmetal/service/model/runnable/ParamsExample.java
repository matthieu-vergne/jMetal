package org.uma.jmetal.service.model.runnable;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Rel;

public class ParamsExample extends ResourceSupport {

	public final String description;

	public ParamsExample(ResourceSupport parent, String parentId, String parentRel) {
		this.description = "Here should appear an example of parameters for " + parentId
				+ ", based on their definition, and ready for copy-paste-run.";
		add(parent.getLink(Rel.PARAMS_EXAMPLE).withRel(Rel.SELF));
		add(parent.getLink(Rel.SELF).withRel(parentRel));
		add(parent.getLink(Rel.PARAMS_DEFINITION));
	}

}
