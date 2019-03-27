package org.uma.jmetal.service.model;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

public class ParamsExample extends ResourceSupport {

	// TODO factor
	public static final String REL_ALGORITHM = "algorithm";

	public final String description;

	public ParamsExample(String algoId) {
		this.description = "Here should appear an example of parameters for " + algoId
				+ ", based on their definition, and ready for copy-paste-run.";
		Algorithm algorithm = new Algorithm(algoId);
		add(algorithm.getLink(Algorithm.REL_PARAMS_EXAMPLE).withRel(Link.REL_SELF));
		add(algorithm.getLink(Link.REL_SELF).withRel(REL_ALGORITHM));
		add(algorithm.getLink(Algorithm.REL_PARAMS_DEFINITION));
	}

}
