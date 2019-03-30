package org.uma.jmetal.service.register.operator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JMetalOperatorsInitializer implements InitializingBean {

	private final OperatorRegister register;

	public JMetalOperatorsInitializer(@Autowired OperatorRegister register) {
		this.register = register;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO retrieve actual operators
		register.add("OP1");
		register.add("OP2");
		register.add("OP3");
	}

}
