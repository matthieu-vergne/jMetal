package org.uma.jmetal.service.controller.algorithm;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uma.jmetal.service.Link;
import org.uma.jmetal.service.controller.runnable.RunnableTemplateController;
import org.uma.jmetal.service.model.algorithm.Algorithm;

@RestController
@RequestMapping("/algorithms")
public class AlgorithmController extends RunnableTemplateController<Algorithm> {

	public AlgorithmController() {
		super("algorithm", Link.REL_ALGORITHM);
	}

	@Override
	protected Collection<String> getAllIds() {
		// TODO retrieve actual algorithms
		return Arrays.asList("ABYSS", "NSGA-2", "NSGA-3");
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
