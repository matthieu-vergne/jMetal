package org.uma.jmetal.service.serial;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.solution.Solution;

public class SolutionSerializer {

	public static <T> Object toVars(Solution<T> solution) {
		return solution.getVariables();
	}

	public static <T> Solution<T> fromVars(Object object) {
		@SuppressWarnings("unchecked")
		List<T> variables = (List<T>) object;
		List<Double> objectives = Collections.emptyList();
		Map<Object, Object> attributes = Collections.emptyMap();
		return new SelfContainedSolution<>(variables, objectives, attributes);
	}

}
