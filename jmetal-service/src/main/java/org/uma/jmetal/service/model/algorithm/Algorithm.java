package org.uma.jmetal.service.model.algorithm;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.function.Function;

import org.uma.jmetal.service.controller.algorithm.AlgorithmController;
import org.uma.jmetal.service.model.runnable.ParamsDefinition;
import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.model.runnable.RunnableTemplate;

public class Algorithm {

	private final Function<Run.Params, Object> function;
	private final ParamsDefinition paramsDefinition;

	public Algorithm(Function<Run.Params, Object> function, ParamsDefinition paramsDefinition) {
		this.function = function;
		this.paramsDefinition = paramsDefinition;
	}

	public Function<Run.Params, Object> getFunction() {
		return function;
	}

	public ParamsDefinition getParamsDefinition() {
		return paramsDefinition;
	}

	public static class Response extends RunnableTemplate.Response {

		public final String description;

		public Response(String id) {
			super(id, methodOn(AlgorithmController.class));
			this.description = "Here you should find a general description of " + id + ".";
			add(linkTo(methodOn(AlgorithmController.class).get(id)).withSelfRel());
		}

	}

}
