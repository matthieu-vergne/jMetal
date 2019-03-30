package org.uma.jmetal.service.controller.algorithms;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.uma.jmetal.service.Link;
import org.uma.jmetal.service.controller.runnable.NoRunException;
import org.uma.jmetal.service.controller.runnable.RunnableController;
import org.uma.jmetal.service.controller.runnable.UnknownRunException;
import org.uma.jmetal.service.model.algorithm.Algorithm;
import org.uma.jmetal.service.model.runnable.ParamsDefinition;
import org.uma.jmetal.service.model.runnable.ParamsExample;
import org.uma.jmetal.service.model.runnable.ResultDefinition;
import org.uma.jmetal.service.model.runnable.ResultExample;
import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.model.runnable.RunParams;
import org.uma.jmetal.service.model.runnable.RunResult;
import org.uma.jmetal.service.model.runnable.RunStatus;

@RestController
@RequestMapping("/algorithms")
public class AlgorithmController implements RunnableController {

	@GetMapping("")
	public @ResponseBody Map<String, ResourceSupport> getAlgorithms() {
		return allAlgorithms().stream().collect(Collectors.toMap(id -> id, id -> {
			ResourceSupport resource = new ResourceSupport();
			resource.add(new Algorithm(id).getLink(Link.REL_SELF));
			return resource;
		}));
	}

	@GetMapping("/{algorithmId}")
	public Algorithm getAlgorithm(@PathVariable String algorithmId) {
		checkIsKnownAlgorithm(algorithmId);
		return new Algorithm(algorithmId);
	}

	@GetMapping("/{algorithmId}/params/definition")
	public ParamsDefinition getParamsDefinition(@PathVariable String algorithmId) {
		checkIsKnownAlgorithm(algorithmId);
		return new ParamsDefinition(new Algorithm(algorithmId), algorithmId, Link.REL_ALGORITHM);
	}

	@GetMapping("/{algorithmId}/params/example")
	public ParamsExample getParamsExample(@PathVariable String algorithmId) {
		checkIsKnownAlgorithm(algorithmId);
		return new ParamsExample(new Algorithm(algorithmId), algorithmId, Link.REL_ALGORITHM);
	}

	@GetMapping("/{algorithmId}/result/definition")
	public ResultDefinition getResultDefinition(@PathVariable String algorithmId) {
		checkIsKnownAlgorithm(algorithmId);
		return new ResultDefinition(new Algorithm(algorithmId), algorithmId, Link.REL_ALGORITHM);
	}

	@GetMapping("/{algorithmId}/result/example")
	public ResultExample getResultExample(@PathVariable String algorithmId) {
		checkIsKnownAlgorithm(algorithmId);
		return new ResultExample(new Algorithm(algorithmId), algorithmId, Link.REL_OPERATOR);
	}

	@GetMapping("/{algorithmId}/runs")
	@Override
	public @ResponseBody Map<Long, ResourceSupport> getRuns(@PathVariable String algorithmId) {
		checkIsKnownAlgorithm(algorithmId);
		Map<Long, ResourceSupport> runs = allRuns(algorithmId).stream().collect(Collectors.toMap(id -> id, id -> {
			ResourceSupport resource = new ResourceSupport();
			resource.add(newRun(algorithmId, id).getLink(Link.REL_SELF));
			return resource;
		}));
		if (runs.isEmpty()) {
			throw new NoRunException(algorithmId);
		} else {
			return runs;
		}
	}

	@GetMapping("/{algorithmId}/runs/{runId}")
	@Override
	public @ResponseBody Run getRun(@PathVariable String algorithmId, @PathVariable long runId) {
		checkIsKnownRun(algorithmId, runId);
		return newRun(algorithmId, runId);
	}

	@GetMapping("/{algorithmId}/runs/{runId}/params")
	@Override
	public @ResponseBody RunParams getRunParams(@PathVariable String algorithmId, @PathVariable long runId) {
		checkIsKnownRun(algorithmId, runId);
		return new RunParams(newRun(algorithmId, runId), algorithmId, runId);
	}

	@GetMapping("/{algorithmId}/runs/{runId}/result")
	@Override
	public @ResponseBody RunResult getRunResult(@PathVariable String algorithmId, @PathVariable long runId) {
		checkIsKnownRun(algorithmId, runId);
		return new RunResult(newRun(algorithmId, runId), algorithmId, runId);
	}

	@GetMapping("/{algorithmId}/runs/{runId}/status")
	@Override
	public @ResponseBody RunStatus getRunStatus(@PathVariable String algorithmId, @PathVariable long runId) {
		checkIsKnownRun(algorithmId, runId);
		return new RunStatus(newRun(algorithmId, runId), algorithmId, runId);
	}

	private List<String> allAlgorithms() {
		// TODO retrieve actual algorithms
		return Arrays.asList("ABYSS", "NSGA-2", "NSGA-3");
	}

	private List<Long> allRuns(String algorithmId) {
		// TODO retrieve actual runs
		if (algorithmId.equals(allAlgorithms().get(1))) {
			return Arrays.asList(123L, 124L, 125L);
		} else {
			return Collections.emptyList();
		}
	}

	private void checkIsKnownAlgorithm(String algorithmId) {
		if (!allAlgorithms().contains(algorithmId)) {
			throw new UnknownAlgorithmException(algorithmId);
		}
	}

	private void checkIsKnownRun(String algorithmId, long runId) {
		checkIsKnownAlgorithm(algorithmId);
		if (!allRuns(algorithmId).contains(runId)) {
			throw new UnknownRunException(algorithmId, runId);
		}
	}

	private Run newRun(String algorithmId, long runId) {
		return new Run(new Algorithm(algorithmId), algorithmId, Link.REL_ALGORITHM, AlgorithmController.class, runId);
	}

}
