package org.uma.jmetal.service.controller.algorithm;

import java.util.Collection;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uma.jmetal.service.Rel;
import org.uma.jmetal.service.controller.runnable.RunnableControllerTemplate;
import org.uma.jmetal.service.executor.RunExecutor;
import org.uma.jmetal.service.model.algorithm.Algorithm;
import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.register.algorithm.AlgorithmRegister;
import org.uma.jmetal.service.register.run.RunRegisterSupplier;

@RestController
@RequestMapping("/algorithms")
public class AlgorithmController extends RunnableControllerTemplate<Algorithm.Response> {

	private final AlgorithmRegister register;

	@Autowired
	public AlgorithmController(AlgorithmRegister register, RunRegisterSupplier runRegisterSupplier,
			RunExecutor executor) {
		super("algorithm", Rel.ALGORITHM, runRegisterSupplier, executor);
		this.register = register;
	}

	@Override
	protected Collection<String> getAllIds() {
		return register.getIds();
	}

	@Override
	protected Algorithm.Response createRunnableResponse(String runnableId) {
		return new Algorithm.Response(runnableId);
	}

	@Override
	protected Function<Run.Params, Object> getRunnableFunction(String runnableId) {
		return register.retrieve(runnableId);
	}

}
