package org.uma.jmetal.service.model.runnable;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Link;

public class ParamsExample extends ResourceSupport {

	public final String description;

	public ParamsExample(ResourceSupport prent, String parentId, String parentRel) {
		this.description = "Here should appear an example of parameters for " + parentId
				+ ", based on their definition, and ready for copy-paste-run.";
		add(prent.getLink(Link.REL_PARAMS_EXAMPLE).withRel(Link.REL_SELF));
		add(prent.getLink(Link.REL_SELF).withRel(parentRel));
		add(prent.getLink(Link.REL_PARAMS_DEFINITION));
	}

}
