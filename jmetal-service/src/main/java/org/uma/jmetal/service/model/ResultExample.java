package org.uma.jmetal.service.model;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

public class ResultExample extends ResourceSupport {

	// TODO factor
	public static final String REL_ALGORITHM = "algorithm";

	public final String description;

	public ResultExample(String algoId) {
		this.description = "Here should appear an example of result for " + algoId + ", based on its definition.";
		Algorithm algorithm = new Algorithm(algoId);
		add(algorithm.getLink(Algorithm.REL_RESULT_EXAMPLE).withRel(Link.REL_SELF));
		add(algorithm.getLink(Link.REL_SELF).withRel(REL_ALGORITHM));
		add(algorithm.getLink(Algorithm.REL_RESULT_DEFINITION));
	}

}
