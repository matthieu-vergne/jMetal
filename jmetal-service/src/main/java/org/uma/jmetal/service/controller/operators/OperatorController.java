package org.uma.jmetal.service.controller.operators;

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
import org.uma.jmetal.service.model.operator.Operator;
import org.uma.jmetal.service.model.runnable.ParamsDefinition;
import org.uma.jmetal.service.model.runnable.ParamsExample;
import org.uma.jmetal.service.model.runnable.ResultDefinition;
import org.uma.jmetal.service.model.runnable.ResultExample;
import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.model.runnable.RunParams;
import org.uma.jmetal.service.model.runnable.RunResult;
import org.uma.jmetal.service.model.runnable.RunStatus;

@RestController
@RequestMapping("/operators")
public class OperatorController implements RunnableController {

	@GetMapping("")
	public @ResponseBody Map<String, ResourceSupport> getOperators() {
		return allOperators().stream().collect(Collectors.toMap(id -> id, id -> {
			ResourceSupport resource = new ResourceSupport();
			resource.add(new Operator(id).getLink(Link.REL_SELF));
			return resource;
		}));
	}

	@GetMapping("/{operatorId}")
	public Operator getOperator(@PathVariable String operatorId) {
		checkIsKnownOperator(operatorId);
		return new Operator(operatorId);
	}

	@GetMapping("/{operatorId}/params/definition")
	public ParamsDefinition getParamsDefinition(@PathVariable String operatorId) {
		checkIsKnownOperator(operatorId);
		return new ParamsDefinition(new Operator(operatorId), operatorId, Link.REL_OPERATOR);
	}

	@GetMapping("/{operatorId}/params/example")
	public ParamsExample getParamsExample(@PathVariable String operatorId) {
		checkIsKnownOperator(operatorId);
		return new ParamsExample(new Operator(operatorId), operatorId, Link.REL_OPERATOR);
	}

	@GetMapping("/{operatorId}/result/definition")
	public ResultDefinition getResultDefinition(@PathVariable String operatorId) {
		checkIsKnownOperator(operatorId);
		return new ResultDefinition(new Operator(operatorId), operatorId, Link.REL_OPERATOR);
	}

	@GetMapping("/{operatorId}/result/example")
	public ResultExample getResultExample(@PathVariable String operatorId) {
		checkIsKnownOperator(operatorId);
		return new ResultExample(new Operator(operatorId), operatorId, Link.REL_OPERATOR);
	}

	@GetMapping("/{operatorId}/runs")
	@Override
	public @ResponseBody Map<Long, ResourceSupport> getRuns(@PathVariable String operatorId) {
		checkIsKnownOperator(operatorId);
		Map<Long, ResourceSupport> runs = allRuns(operatorId).stream().collect(Collectors.toMap(id -> id, id -> {
			ResourceSupport resource = new ResourceSupport();
			resource.add(newRun(operatorId, id).getLink(Link.REL_SELF));
			return resource;
		}));
		if (runs.isEmpty()) {
			throw new NoRunException(operatorId);
		} else {
			return runs;
		}
	}

	@GetMapping("/{operatorId}/runs/{runId}")
	@Override
	public @ResponseBody Run getRun(@PathVariable String operatorId, @PathVariable long runId) {
		checkIsKnownRun(operatorId, runId);
		return newRun(operatorId, runId);
	}

	@GetMapping("/{operatorId}/runs/{runId}/params")
	@Override
	public @ResponseBody RunParams getRunParams(@PathVariable String operatorId, @PathVariable long runId) {
		checkIsKnownRun(operatorId, runId);
		return new RunParams(newRun(operatorId, runId), operatorId, runId);
	}

	@GetMapping("/{operatorId}/runs/{runId}/result")
	@Override
	public @ResponseBody RunResult getRunResult(@PathVariable String operatorId, @PathVariable long runId) {
		checkIsKnownRun(operatorId, runId);
		return new RunResult(newRun(operatorId, runId), operatorId, runId);
	}

	@GetMapping("/{operatorId}/runs/{runId}/status")
	@Override
	public @ResponseBody RunStatus getRunStatus(@PathVariable String operatorId, @PathVariable long runId) {
		checkIsKnownRun(operatorId, runId);
		return new RunStatus(newRun(operatorId, runId), operatorId, runId);
	}

	private List<String> allOperators() {
		// TODO retrieve actual operators
		return Arrays.asList("OP1", "OP2", "OP3");
	}

	private List<Long> allRuns(String operatorId) {
		// TODO retrieve actual runs
		if (operatorId.equals(allOperators().get(1))) {
			return Arrays.asList(123L, 124L, 125L);
		} else {
			return Collections.emptyList();
		}
	}

	private void checkIsKnownOperator(String operatorId) {
		if (!allOperators().contains(operatorId)) {
			throw new UnknownOperatorException(operatorId);
		}
	}

	private void checkIsKnownRun(String operatorId, long runId) {
		checkIsKnownOperator(operatorId);
		if (!allRuns(operatorId).contains(runId)) {
			throw new UnknownRunException(operatorId, runId);
		}
	}

	private Run newRun(String operatorId, long runId) {
		return new Run(new Operator(operatorId), operatorId, Link.REL_OPERATOR, OperatorController.class, runId);
	}

}
