package org.uma.jmetal.service.model.algorithm;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.uma.jmetal.service.controller.algorithm.AlgorithmController;
import org.uma.jmetal.service.model.runnable.RunnableTemplate;

public class Algorithm {

	public static class Response extends RunnableTemplate.Response {

		public final String description;

		public Response(String id) {
			super(id, methodOn(AlgorithmController.class));
			this.description = "Here you should find a general description of " + id + ".";
			add(linkTo(methodOn(AlgorithmController.class).get(id)).withSelfRel());
		}

	}

}
