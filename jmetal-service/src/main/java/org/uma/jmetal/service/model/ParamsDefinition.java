package org.uma.jmetal.service.model;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

public class ParamsDefinition extends ResourceSupport {

	// TODO factor
	public static final String REL_ALGORITHM = "algorithm";

	public final String description;

	public ParamsDefinition(String algoId) {
		this.description = "Here should appear some definitions of the various parameters of " + algoId + ".";
		Algorithm algorithm = new Algorithm(algoId);
		add(algorithm.getLink(Algorithm.REL_PARAMS_DEFINITION).withRel(Link.REL_SELF));
		add(algorithm.getLink(Link.REL_SELF).withRel(REL_ALGORITHM));
		add(algorithm.getLink(Algorithm.REL_PARAMS_EXAMPLE));
	}

}
