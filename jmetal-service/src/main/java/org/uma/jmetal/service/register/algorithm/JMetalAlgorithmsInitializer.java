package org.uma.jmetal.service.register.algorithm;

import java.util.function.Function;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.uma.jmetal.service.model.runnable.Run;

@Component
public class JMetalAlgorithmsInitializer implements InitializingBean {

	private final AlgorithmRegister register;

	public JMetalAlgorithmsInitializer(@Autowired AlgorithmRegister register) {
		this.register = register;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO retrieve actual operators
		Function<Run.Params, Object> notImplemented = params -> {
			throw new RuntimeException("Not implemented yet");
		};
		register.store("ABYSS", notImplemented);
		register.store("NSGA-2", notImplemented);
		register.store("NSGA-3", notImplemented);
	}

}
