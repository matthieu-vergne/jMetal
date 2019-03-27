package org.uma.jmetal.service.model;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.controller.AlgorithmController;

public class Algorithm extends ResourceSupport {

	public static final String REL_PARAMS_DEFINITION = "params definition";
	public static final String REL_PARAMS_EXAMPLE = "params example";
	public static final String REL_RESULT_DEFINITION = "result definition";
	public static final String REL_RESULT_EXAMPLE = "result example";
	public static final String REL_RUNS = "runs";

	public final String description;

	public Algorithm(String id) {
		this.description = "Here you should find a general description of " + id + ".";
		add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(id)).withSelfRel());
		add(linkTo(methodOn(AlgorithmController.class).getAlgorithmParamsDefinition(id))
				.withRel(REL_PARAMS_DEFINITION));
		add(linkTo(methodOn(AlgorithmController.class).getAlgorithmParamsExample(id)).withRel(REL_PARAMS_EXAMPLE));
		add(linkTo(methodOn(AlgorithmController.class).getAlgorithmResultDefinition(id))
				.withRel(REL_RESULT_DEFINITION));
		add(linkTo(methodOn(AlgorithmController.class).getAlgorithmResultExample(id)).withRel(REL_RESULT_EXAMPLE));
		add(linkTo(methodOn(AlgorithmController.class).getAlgorithmRuns(id)).withRel(REL_RUNS));
	}

}
