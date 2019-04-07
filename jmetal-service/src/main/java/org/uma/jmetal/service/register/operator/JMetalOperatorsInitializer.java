package org.uma.jmetal.service.register.operator;

import java.util.Arrays;
import java.util.function.Function;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.uma.jmetal.service.model.operator.Operator;
import org.uma.jmetal.service.model.runnable.ParamDefinition;
import org.uma.jmetal.service.model.runnable.ParamsDefinition;
import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.model.runnable.Run.Params;

@Component
public class JMetalOperatorsInitializer implements InitializingBean {

	private final OperatorRegister register;

	public JMetalOperatorsInitializer(@Autowired OperatorRegister register) {
		this.register = register;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO accept arbitrary result (auto-translation)
		// TODO exception if request parameters do not fit
		// TODO retrieve actual operators
		Function<Function<Run.Params, Object>, Function<Run.Params, Object>> opBuilder = result -> params -> {
			try {
				Thread.sleep(3000); // Wait 3s before to terminate
			} catch (InterruptedException cause) {
				throw new RuntimeException(cause);
			}
			return result.apply(params);
		};

		register.store("empty-op", opBuilder.apply(params -> "Operator has been run"));

		register.store("failed-op", opBuilder.apply(params -> {
			throw new RuntimeException("Operator has failed");
		}));

		ParamDefinition<String> content = ParamDefinition.string("content").withExample("some content");
		ParamsDefinition paramsDefinition = new ParamsDefinition(Arrays.asList(content));
		Function<Params, Object> function = opBuilder.apply(params -> "I received: " + content.getValue(params));
		register.store("simple-op", new Operator(function, paramsDefinition));
	}

}
