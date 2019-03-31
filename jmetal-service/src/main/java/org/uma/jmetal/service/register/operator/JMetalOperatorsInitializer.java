package org.uma.jmetal.service.register.operator;

import java.util.function.Function;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.uma.jmetal.service.model.runnable.Run;

@Component
public class JMetalOperatorsInitializer implements InitializingBean {

	private final OperatorRegister register;

	public JMetalOperatorsInitializer(@Autowired OperatorRegister register) {
		this.register = register;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO accept arbitrary parameters (auto-translation)
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
		register.store("simple-op", opBuilder.apply(params -> "I received: " + params.get("content")));
		register.store("failed-op", opBuilder.apply(params -> {
			throw new RuntimeException("Operator has failed");
		}));
	}

}
