package org.uma.jmetal.service.controller.operator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uma.jmetal.service.Link;
import org.uma.jmetal.service.controller.runnable.RunnableTemplateController;
import org.uma.jmetal.service.model.operator.Operator;

@RestController
@RequestMapping("/operators")
public class OperatorController extends RunnableTemplateController<Operator> {

	public OperatorController() {
		super("operator", Link.REL_OPERATOR);
	}

	@Override
	protected Collection<String> getAllIds() {
		// TODO retrieve actual operators
		return Arrays.asList("OP1", "OP2", "OP3");
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
