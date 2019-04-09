package org.uma.jmetal.service.model.algorithm;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.function.Function;

import org.uma.jmetal.service.controller.algorithm.AlgorithmController;
import org.uma.jmetal.service.model.runnable.ParamsDefinition;
import org.uma.jmetal.service.model.runnable.ResultDefinition;
import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.model.runnable.RunnableTemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Algorithm<T> {

	private final Function<Run.Params, T> function;
	private final ParamsDefinition paramsDefinition;
	private final ResultDefinition<T> resultDefinition;

	public Algorithm(ParamsDefinition paramsDefinition, Function<Run.Params, T> function,
			ResultDefinition<T> resultDefinition) {
		this.function = function;
		this.paramsDefinition = paramsDefinition;
		this.resultDefinition = resultDefinition;
	}

	@JsonIgnore
	public Function<Run.Params, T> getFunction() {
		return function;
	}

	@JsonProperty("params")
	public ParamsDefinition getParamsDefinition() {
		return paramsDefinition;
	}

	@JsonProperty("result")
	public ResultDefinition<T> getResultDefinition() {
		return resultDefinition;
	}

	@JsonIgnore
	public Function<Run.Params, Object> getGenericFunction() {
		return params -> resultDefinition.getType().toExposedType(function.apply(params));
	}

	public static class Response extends RunnableTemplate.Response {

		public final Algorithm<?> algorithm;

		public Response(String id, Algorithm<?> algorithm) {
			super(id, methodOn(AlgorithmController.class));
			this.algorithm = algorithm;
			add(linkTo(methodOn(AlgorithmController.class).get(id)).withSelfRel());
		}

	}

}
