package org.uma.jmetal.service.model.algorithm;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Link;
import org.uma.jmetal.service.controller.algorithms.AlgorithmController;

public class Algorithm extends ResourceSupport {

	public final String description;

	public Algorithm(String id) {
		this.description = "Here you should find a general description of " + id + ".";
		AlgorithmController controller = methodOn(AlgorithmController.class);
		add(linkTo(controller.getAlgorithm(id)).withSelfRel());
		add(linkTo(controller.getParamsDefinition(id)).withRel(Link.REL_PARAMS_DEFINITION));
		add(linkTo(controller.getParamsExample(id)).withRel(Link.REL_PARAMS_EXAMPLE));
		add(linkTo(controller.getResultDefinition(id)).withRel(Link.REL_RESULT_DEFINITION));
		add(linkTo(controller.getResultExample(id)).withRel(Link.REL_RESULT_EXAMPLE));
		add(linkTo(controller.getRuns(id)).withRel(Link.REL_RUNS));
	}

}
