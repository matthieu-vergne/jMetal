package org.uma.jmetal.service.register.operator;

import static org.uma.jmetal.service.model.runnable.ExposedType.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.uma.jmetal.operator.impl.crossover.NullCrossover;
import org.uma.jmetal.operator.impl.mutation.NullMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.impl.ComposableDoubleProblem;
import org.uma.jmetal.service.model.operator.Operator;
import org.uma.jmetal.service.model.runnable.ExposedType;
import org.uma.jmetal.service.model.runnable.ParamDefinition;
import org.uma.jmetal.service.model.runnable.ParamsDefinition;
import org.uma.jmetal.service.model.runnable.ResultDefinition;
import org.uma.jmetal.service.model.runnable.Run.Params;
import org.uma.jmetal.solution.Solution;

@Component
public class JMetalOperatorsInitializer implements InitializingBean {

	private final OperatorRegister register;

	public JMetalOperatorsInitializer(@Autowired OperatorRegister register) {
		this.register = register;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO exception if request parameters do not fit
		// TODO retrieve actual operators
		{
			ParamsDefinition paramsDef = ParamsDefinition.empty();
			Function<Params, String> function = params -> "Operator has been run";
			ResultDefinition<String> resultDef = ResultDefinition.of(STRING).withExample("Operator has been run");
			register.store("no-param-op", new Operator<>(paramsDef, function, resultDef));
		}

		{
			ParamsDefinition paramsDef = ParamsDefinition.empty();
			Function<Params, Void> function = params -> {
				throw new RuntimeException("Operator has failed");
			};
			ResultDefinition<Void> resultDef = ResultDefinition.empty();
			register.store("failed-op", new Operator<>(paramsDef, function, resultDef));
		}

		{
			ParamDefinition<String> content = ParamDefinition.of(STRING, "content").withExample("some content");
			ParamsDefinition paramsDef = new ParamsDefinition(content);
			Function<Params, String> function = params -> {
				try {
					Thread.sleep(3000); // Wait 3s before to terminate
				} catch (InterruptedException cause) {
					throw new RuntimeException(cause);
				}
				return "I received: " + content.from(params);
			};
			ResultDefinition<String> resultDef = ResultDefinition.of(STRING).withExample("I received: some content");
			register.store("3s-op", new Operator<>(paramsDef, function, resultDef));
		}

		Problem<? extends Solution<Double>> problem = new ComposableDoubleProblem().addVariable(0, 100)
				.addVariable(0, 100).addVariable(0, 100);
		Solution<Double> solutionEx1 = problem.createSolution();
		Solution<Double> solutionEx2 = problem.createSolution();
		{
			ExposedType<Solution<Double>> solutionVars = solutionVars(DOUBLE);
			ParamDefinition<Solution<Double>> source = ParamDefinition.of(solutionVars, "source")
					.withExample(solutionEx1);
			ParamsDefinition paramsDef = new ParamsDefinition(source);
			Function<Params, Solution<Double>> function = params -> new NullMutation<Solution<Double>>()
					.execute(source.from(params));
			ResultDefinition<Solution<Double>> resultDef = ResultDefinition.of(solutionVars).withExample(solutionEx1);
			register.store("null-mutation", new Operator<>(paramsDef, function, resultDef));
		}

		{
			ExposedType<List<Solution<Double>>> list = list(solutionVars(DOUBLE));
			List<Solution<Double>> example = Arrays.asList(solutionEx1, solutionEx2);
			ParamDefinition<List<Solution<Double>>> source = ParamDefinition.of(list, "source").withExample(example);
			ParamsDefinition paramsDef = new ParamsDefinition(source);
			Function<Params, List<Solution<Double>>> function = params -> new NullCrossover<Solution<Double>>()
					.execute(source.from(params));
			ResultDefinition<List<Solution<Double>>> resultDef = ResultDefinition.of(list).withExample(example);
			register.store("null-crossover", new Operator<>(paramsDef, function, resultDef));
		}
	}

}
