package org.uma.jmetal.service.model;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.controller.AlgorithmController;

public class Run extends ResourceSupport {

	// TODO factor
	public static final String REL_ALGORITHM = "algorithm";
	public static final String REL_RUN_PARAMS = "params";
	public static final String REL_RUN_RESULT = "result";
	public static final String REL_RUN_STATUS = "status";

	public final String description;

	public Run(String algoId, long runId) {
		this.description = "Here may appear a summary of the run " + runId + " of " + algoId + ".";
		Algorithm algorithm = new Algorithm(algoId);
		add(linkTo(methodOn(AlgorithmController.class).getAlgorithmRun(algoId, runId)).withSelfRel());
		add(linkTo(methodOn(AlgorithmController.class).getAlgorithmRunParams(algoId, runId)).withRel(REL_RUN_PARAMS));
		add(linkTo(methodOn(AlgorithmController.class).getAlgorithmRunResult(algoId, runId)).withRel(REL_RUN_RESULT));
		add(linkTo(methodOn(AlgorithmController.class).getAlgorithmRunStatus(algoId, runId)).withRel(REL_RUN_STATUS));
		add(algorithm.getLink(Link.REL_SELF).withRel(REL_ALGORITHM));
	}

}
