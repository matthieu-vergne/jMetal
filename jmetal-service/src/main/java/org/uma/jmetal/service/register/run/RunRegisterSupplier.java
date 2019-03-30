package org.uma.jmetal.service.register.run;

import org.springframework.stereotype.Component;

@Component
public class RunRegisterSupplier {

	public RunRegister get(String runnableType, String runnableId) {
		RunRegister register = new RunRegister();
		// TODO start empty or retrieve from persistence
		if (runnableType.equals("algorithm") && runnableId.equals("NSGA-2")
				|| runnableType.equals("operator") && runnableId.equals("OP2")) {
			register.add(123L);
			register.add(124L);
			register.add(125L);
		}
		return register;
	}

}
