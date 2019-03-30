package org.uma.jmetal.service.model.runnable;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Link;

public class ParamsExample extends ResourceSupport {

	public final String description;

	public ParamsExample(ResourceSupport parent, String parentId, String parentRel) {
		this.description = "Here should appear an example of parameters for " + parentId
				+ ", based on their definition, and ready for copy-paste-run.";
		add(parent.getLink(Link.REL_PARAMS_EXAMPLE).withRel(Link.REL_SELF));
		add(parent.getLink(Link.REL_SELF).withRel(parentRel));
		add(parent.getLink(Link.REL_PARAMS_DEFINITION));
	}

}
