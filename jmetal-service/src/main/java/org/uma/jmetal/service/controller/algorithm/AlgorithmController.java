package org.uma.jmetal.service.controller.algorithm;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uma.jmetal.service.Link;
import org.uma.jmetal.service.controller.runnable.RunnableControllerTemplate;
import org.uma.jmetal.service.model.algorithm.Algorithm;
import org.uma.jmetal.service.register.algorithm.AlgorithmRegister;

@RestController
@RequestMapping("/algorithms")
public class AlgorithmController extends RunnableControllerTemplate<Algorithm> {

	private final AlgorithmRegister register;

	public AlgorithmController(@Autowired AlgorithmRegister register) {
		super("algorithm", Link.REL_ALGORITHM);
		this.register = register;
	}

	@Override
	protected Collection<String> getAllIds() {
		return register.getIds();
	}

	@Override
	protected Algorithm createRunnable(String runnableId) {
		return new Algorithm(runnableId);
	}

	@Override
	protected Collection<Long> getAllRuns(String runnableId) {
		// TODO retrieve actual runs
		if (runnableId.equals("NSGA-2")) {
			return Arrays.asList(123L, 124L, 125L);
		} else {
			return Collections.emptyList();
		}
	}

}
