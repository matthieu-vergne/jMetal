package org.uma.jmetal.service.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.uma.jmetal.service.model.Algorithm;
import org.uma.jmetal.service.model.ParamsDefinition;
import org.uma.jmetal.service.model.ParamsExample;
import org.uma.jmetal.service.model.ResultDefinition;
import org.uma.jmetal.service.model.ResultExample;
import org.uma.jmetal.service.model.Run;
import org.uma.jmetal.service.model.RunParams;
import org.uma.jmetal.service.model.RunResult;
import org.uma.jmetal.service.model.RunStatus;

@RestController
@RequestMapping("/algorithms")
public class AlgorithmController {

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
	public ParamsDefinition getAlgorithmParamsDefinition(@PathVariable String algorithmId) {
		checkIsKnownAlgorithm(algorithmId);
		return new ParamsDefinition(algorithmId);
	}

	@GetMapping("/{algorithmId}/params/example")
	public ParamsExample getAlgorithmParamsExample(@PathVariable String algorithmId) {
		checkIsKnownAlgorithm(algorithmId);
		return new ParamsExample(algorithmId);
	}

	@GetMapping("/{algorithmId}/result/definition")
	public ResultDefinition getAlgorithmResultDefinition(@PathVariable String algorithmId) {
		checkIsKnownAlgorithm(algorithmId);
		return new ResultDefinition(algorithmId);
	}

	@GetMapping("/{algorithmId}/result/example")
	public ResultExample getAlgorithmResultExample(@PathVariable String algorithmId) {
		checkIsKnownAlgorithm(algorithmId);
		return new ResultExample(algorithmId);
	}

	@GetMapping("/{algorithmId}/runs")
	public @ResponseBody Map<Long, ResourceSupport> getAlgorithmRuns(@PathVariable String algorithmId) {
		checkIsKnownAlgorithm(algorithmId);
		Map<Long, ResourceSupport> runs = allRuns(algorithmId).stream().collect(Collectors.toMap(id -> id, id -> {
			ResourceSupport resource = new ResourceSupport();
			resource.add(new Run(algorithmId, id).getLink(Link.REL_SELF));
			return resource;
		}));
		if (runs.isEmpty()) {
			throw new NoRunException(algorithmId);
		} else {
			return runs;
		}
	}

	@GetMapping("/{algorithmId}/runs/{runId}")
	public @ResponseBody Run getAlgorithmRun(@PathVariable String algorithmId, @PathVariable long runId) {
		checkIsKnownRun(algorithmId, runId);
		return new Run(algorithmId, runId);
	}

	@GetMapping("/{algorithmId}/runs/{runId}/params")
	public @ResponseBody RunParams getAlgorithmRunParams(@PathVariable String algorithmId, @PathVariable long runId) {
		checkIsKnownRun(algorithmId, runId);
		return new RunParams(algorithmId, runId);
	}

	@GetMapping("/{algorithmId}/runs/{runId}/result")
	public @ResponseBody RunResult getAlgorithmRunResult(@PathVariable String algorithmId, @PathVariable long runId) {
		checkIsKnownRun(algorithmId, runId);
		return new RunResult(algorithmId, runId);
	}

	@GetMapping("/{algorithmId}/runs/{runId}/status")
	public @ResponseBody RunStatus getAlgorithmRunStatus(@PathVariable String algorithmId, @PathVariable long runId) {
		checkIsKnownRun(algorithmId, runId);
		return new RunStatus(algorithmId, runId);
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
			throw new UnknownAlgorithmRunException(algorithmId, runId);
		}
	}

}
