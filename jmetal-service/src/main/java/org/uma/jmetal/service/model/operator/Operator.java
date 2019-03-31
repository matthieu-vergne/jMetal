package org.uma.jmetal.service.model.operator;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.uma.jmetal.service.controller.operator.OperatorController;
import org.uma.jmetal.service.model.runnable.RunnableTemplate;

public class Operator {

	public static class Response extends RunnableTemplate.Response {

		public final String description;

		public Response(String id) {
			super(id, methodOn(OperatorController.class));
			this.description = "Here you should find a general description of " + id + ".";
			add(linkTo(methodOn(OperatorController.class).get(id)).withSelfRel());
		}

	}

}
