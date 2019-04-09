package org.uma.jmetal.service.model.operator;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.function.Function;

import org.uma.jmetal.service.controller.operator.OperatorController;
import org.uma.jmetal.service.model.runnable.ParamsDefinition;
import org.uma.jmetal.service.model.runnable.ResultDefinition;
import org.uma.jmetal.service.model.runnable.Run.Params;
import org.uma.jmetal.service.model.runnable.RunnableTemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Operator<T> {

	private final Function<Params, T> function;
	private final ParamsDefinition paramsDefinition;
	private final ResultDefinition<T> resultDefinition;

	public Operator(ParamsDefinition paramsDefinition, Function<Params, T> function,
			ResultDefinition<T> resultDefinition) {
		this.function = function;
		this.paramsDefinition = paramsDefinition;
		this.resultDefinition = resultDefinition;
	}

	@JsonIgnore
	public Function<Params, T> getFunction() {
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
	public Function<Params, Object> getGenericFunction() {
		return params -> resultDefinition.getType().toExposedType(function.apply(params));
	}

	public static class Response extends RunnableTemplate.Response {

		public final Operator<?> operator;

		public Response(String id, Operator<?> operator) {
			super(id, methodOn(OperatorController.class));
			this.operator = operator;
			add(linkTo(methodOn(OperatorController.class).get(id)).withSelfRel());
		}

	}

}
