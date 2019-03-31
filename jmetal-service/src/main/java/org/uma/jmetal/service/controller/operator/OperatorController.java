package org.uma.jmetal.service.controller.operator;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uma.jmetal.service.Rel;
import org.uma.jmetal.service.controller.runnable.RunnableControllerTemplate;
import org.uma.jmetal.service.model.operator.Operator;
import org.uma.jmetal.service.register.operator.OperatorRegister;
import org.uma.jmetal.service.register.run.RunRegisterSupplier;

@RestController
@RequestMapping("/operators")
public class OperatorController extends RunnableControllerTemplate<Operator.Response> {

	private final OperatorRegister register;

	@Autowired
	public OperatorController(OperatorRegister register, RunRegisterSupplier runRegisterSupplier) {
		super("operator", Rel.OPERATOR, runRegisterSupplier);
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

}
