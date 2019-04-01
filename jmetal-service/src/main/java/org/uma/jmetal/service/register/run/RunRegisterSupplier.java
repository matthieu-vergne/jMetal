package org.uma.jmetal.service.register.run;

import org.springframework.stereotype.Component;
import org.uma.jmetal.service.model.runnable.Run;

@Component
public class RunRegisterSupplier {

	public RunRegister get(String runnableType, String runnableId) {
		RunRegister register = new RunRegister();
		// TODO start empty or retrieve from persistence
		if (runnableType.equals("algorithm") && runnableId.equals("NSGA-2")) {
			register.store(123L, new Run(new Run.Request(), Run.Result.withValue("some result")));
			register.store(124L, new Run(new Run.Request(), Run.Result.withValue("some other result")));
			register.store(125L, new Run(new Run.Request(), Run.Result.withError(new Exception("failed run"))));
		}
		return register;
	}

}
