package org.uma.jmetal.service.register.operator;

import static org.uma.jmetal.service.model.runnable.ExposedType.*;

import java.util.Arrays;
import java.util.function.Function;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.uma.jmetal.service.model.operator.Operator;
import org.uma.jmetal.service.model.runnable.ExposedType;
import org.uma.jmetal.service.model.runnable.ParamDefinition;
import org.uma.jmetal.service.model.runnable.ParamsDefinition;
import org.uma.jmetal.service.model.runnable.ResultDefinition;
import org.uma.jmetal.service.model.runnable.Run.Params;

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
	}

}
