package org.uma.jmetal.service.model.operator;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Link;
import org.uma.jmetal.service.controller.operators.OperatorController;

public class Operator extends ResourceSupport {

	public final String description;

	public Operator(String id) {
		this.description = "Here you should find a general description of " + id + ".";
		OperatorController controller = methodOn(OperatorController.class);
		add(linkTo(controller.get(id)).withSelfRel());
		add(linkTo(controller.getParamsDefinition(id)).withRel(Link.REL_PARAMS_DEFINITION));
		add(linkTo(controller.getParamsExample(id)).withRel(Link.REL_PARAMS_EXAMPLE));
		add(linkTo(controller.getResultDefinition(id)).withRel(Link.REL_RESULT_DEFINITION));
		add(linkTo(controller.getResultExample(id)).withRel(Link.REL_RESULT_EXAMPLE));
		add(linkTo(controller.getRuns(id)).withRel(Link.REL_RUNS));
	}

}
