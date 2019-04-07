package org.uma.jmetal.service.controller.operator;

import java.util.Collection;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uma.jmetal.service.Rel;
import org.uma.jmetal.service.controller.runnable.RunnableControllerTemplate;
import org.uma.jmetal.service.executor.RunExecutor;
import org.uma.jmetal.service.model.operator.Operator;
import org.uma.jmetal.service.model.runnable.ParamsDefinition;
import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.register.operator.OperatorRegister;
import org.uma.jmetal.service.register.run.RunRegisterSupplier;

@RestController
@RequestMapping("/operators")
public class OperatorController extends RunnableControllerTemplate<Operator.Response> {

	private final OperatorRegister register;

	@Autowired
	public OperatorController(OperatorRegister register, RunRegisterSupplier runRegisterSupplier,
			RunExecutor executor) {
		super("operator", Rel.OPERATOR, runRegisterSupplier, executor);
		this.register = register;
	}

	@Override
	protected Collection<String> getAllIds() {
		return register.getIds();
	}

	@Override
	protected Operator.Response createRunnableResponse(String runnableId) {
		return new Operator.Response(runnableId);
	}

	@Override
	protected Function<Run.Params, Object> getRunnableFunction(String runnableId) {
		return register.retrieve(runnableId).getFunction();
	}
	
	@Override
	protected ParamsDefinition getRunnableParamsDefinition(String runnableId) {
		return register.retrieve(runnableId).getParamsDefinition();
	}

}
