package org.uma.jmetal.service.model.operator;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.function.Function;

import org.uma.jmetal.service.controller.operator.OperatorController;
import org.uma.jmetal.service.model.runnable.ParamsDefinition;
import org.uma.jmetal.service.model.runnable.Run.Params;
import org.uma.jmetal.service.model.runnable.RunnableTemplate;

public class Operator {

	private final Function<Params, Object> function;
	private final ParamsDefinition paramsDefinition;

	public Operator(Function<Params, Object> function, ParamsDefinition paramsDefinition) {
		this.function = function;
		this.paramsDefinition = paramsDefinition;
	}
	
	public Function<Params, Object> getFunction() {
		return function;
	}
	
	public ParamsDefinition getParamsDefinition() {
		return paramsDefinition;
	}

	public static class Response extends RunnableTemplate.Response {

		public final String description;

		public Response(String id) {
			super(id, methodOn(OperatorController.class));
			this.description = "Here you should find a general description of " + id + ".";
			add(linkTo(methodOn(OperatorController.class).get(id)).withSelfRel());
		}

	}

}
