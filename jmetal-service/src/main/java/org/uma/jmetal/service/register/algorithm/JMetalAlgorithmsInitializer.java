package org.uma.jmetal.service.register.algorithm;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JMetalAlgorithmsInitializer implements InitializingBean {

	private final AlgorithmRegister register;

	public JMetalAlgorithmsInitializer(@Autowired AlgorithmRegister register) {
		this.register = register;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO retrieve actual operators
		register.add("ABYSS");
		register.add("NSGA-2");
		register.add("NSGA-3");
	}

}
