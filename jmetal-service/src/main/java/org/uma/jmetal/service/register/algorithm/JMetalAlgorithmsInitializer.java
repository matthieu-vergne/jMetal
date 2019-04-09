package org.uma.jmetal.service.register.algorithm;

import java.util.function.Function;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.uma.jmetal.service.model.algorithm.Algorithm;
import org.uma.jmetal.service.model.runnable.ParamsDefinition;
import org.uma.jmetal.service.model.runnable.ResultDefinition;
import org.uma.jmetal.service.model.runnable.Run;

@Component
public class JMetalAlgorithmsInitializer implements InitializingBean {

	private final AlgorithmRegister register;

	public JMetalAlgorithmsInitializer(@Autowired AlgorithmRegister register) {
		this.register = register;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ParamsDefinition paramsDef = ParamsDefinition.empty();
		Function<Run.Params, Void> function = params -> {
			throw new UnsupportedOperationException("Not implemented yet");
		};
		ResultDefinition<Void> resultDef = ResultDefinition.empty();
		Algorithm<?> notImplemented = new Algorithm<>(paramsDef, function, resultDef);

		// TODO retrieve actual algorithms
		register.store("ABYSS", notImplemented);
		register.store("NSGA-2", notImplemented);
		register.store("NSGA-3", notImplemented);
	}

}
