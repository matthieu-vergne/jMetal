package org.uma.jmetal.service.controller.operator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uma.jmetal.service.Link;
import org.uma.jmetal.service.controller.runnable.RunnableControllerTemplate;
import org.uma.jmetal.service.model.operator.Operator;
import org.uma.jmetal.service.register.operator.OperatorRegister;

@RestController
@RequestMapping("/operators")
public class OperatorController extends RunnableControllerTemplate<Operator> {

	private final OperatorRegister register;

	public OperatorController(@Autowired OperatorRegister register) {
		super("operator", Link.REL_OPERATOR);
		this.register = register;
	}

	@Override
	protected Collection<String> getAllIds() {
		return register.getIds();
	}

	@Override
	protected Operator createRunnable(String runnableId) {
		return new Operator(runnableId);
	}

	@Override
	protected Collection<Long> getAllRuns(String runnableId) {
		// TODO retrieve actual runs
		if (runnableId.equals("OP2")) {
			return Arrays.asList(123L, 124L, 125L);
		} else {
			return Collections.emptyList();
		}
	}

}
