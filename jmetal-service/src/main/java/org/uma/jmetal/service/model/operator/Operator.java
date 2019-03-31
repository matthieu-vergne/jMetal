package org.uma.jmetal.service.model.operator;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Rel;
import org.uma.jmetal.service.controller.operator.OperatorController;

public class Operator {

	public static class Response extends ResourceSupport {

		public final String description;

		public Response(String id) {
			this.description = "Here you should find a general description of " + id + ".";
			OperatorController controller = methodOn(OperatorController.class);
			add(linkTo(controller.get(id)).withSelfRel());
			add(linkTo(controller.getParamsDefinition(id)).withRel(Rel.PARAMS_DEFINITION));
			add(linkTo(controller.getParamsExample(id)).withRel(Rel.PARAMS_EXAMPLE));
			add(linkTo(controller.getResultDefinition(id)).withRel(Rel.RESULT_DEFINITION));
			add(linkTo(controller.getResultExample(id)).withRel(Rel.RESULT_EXAMPLE));
			add(linkTo(controller.getRuns(id)).withRel(Rel.RUNS));
		}

	}

}
