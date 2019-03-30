package org.uma.jmetal.service.controller.runnable;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uma.jmetal.service.Link;
import org.uma.jmetal.service.model.runnable.ParamsDefinition;
import org.uma.jmetal.service.model.runnable.ParamsExample;
import org.uma.jmetal.service.model.runnable.ResultDefinition;
import org.uma.jmetal.service.model.runnable.ResultExample;
import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.model.runnable.RunParams;
import org.uma.jmetal.service.model.runnable.RunResult;
import org.uma.jmetal.service.model.runnable.RunStatus;

public abstract class RunnableTemplateController<T extends ResourceSupport> implements RunnableController {

	private final String runnableRel;
	private final String runnableType;

	public RunnableTemplateController(String runnableType, String runnableRel) {
		this.runnableRel = runnableRel;
		this.runnableType = runnableType;
	}

	protected abstract Collection<String> getAllIds();

	protected abstract T createRunnable(String runnableId);

	protected abstract Collection<Long> getAllRuns(String runnableId);

	@GetMapping("")
	public @ResponseBody Map<String, ResourceSupport> getAll() {
		return getAllIds().stream().collect(Collectors.toMap(id -> id, id -> {
			ResourceSupport resource = new ResourceSupport();
			resource.add(createRunnable(id).getLink(Link.REL_SELF));
			return resource;
		}));
	}

	@GetMapping("/{runnableId}")
	public T get(@PathVariable String runnableId) {
		checkIsKnownRunnable(runnableId);
		return createRunnable(runnableId);
	}

	@GetMapping("/{runnableId}/params/definition")
	public ParamsDefinition getParamsDefinition(@PathVariable String runnableId) {
		checkIsKnownRunnable(runnableId);
		return new ParamsDefinition(createRunnable(runnableId), runnableId, Link.REL_OPERATOR);
	}

	@GetMapping("/{runnableId}/params/example")
	public ParamsExample getParamsExample(@PathVariable String runnableId) {
		checkIsKnownRunnable(runnableId);
		return new ParamsExample(createRunnable(runnableId), runnableId, Link.REL_OPERATOR);
	}

	@GetMapping("/{runnableId}/result/definition")
	public ResultDefinition getResultDefinition(@PathVariable String runnableId) {
		checkIsKnownRunnable(runnableId);
		return new ResultDefinition(createRunnable(runnableId), runnableId, Link.REL_OPERATOR);
	}

	@GetMapping("/{runnableId}/result/example")
	public ResultExample getResultExample(@PathVariable String runnableId) {
		checkIsKnownRunnable(runnableId);
		return new ResultExample(createRunnable(runnableId), runnableId, Link.REL_OPERATOR);
	}

	@GetMapping("/{runnableId}/runs")
	@Override
	public @ResponseBody Map<Long, ResourceSupport> getRuns(@PathVariable String runnableId) {
		checkIsKnownRunnable(runnableId);
		Map<Long, ResourceSupport> runs = getAllRuns(runnableId).stream().collect(Collectors.toMap(id -> id, id -> {
			ResourceSupport resource = new ResourceSupport();
			resource.add(newRun(runnableId, id).getLink(Link.REL_SELF));
			return resource;
		}));
		if (runs.isEmpty()) {
			throw new NoRunException(runnableId);
		} else {
			return runs;
		}
	}

	@GetMapping("/{runnableId}/runs/{runId}")
	@Override
	public @ResponseBody Run getRun(@PathVariable String runnableId, @PathVariable long runId) {
		checkIsKnownRun(runnableId, runId);
		return newRun(runnableId, runId);
	}

	@GetMapping("/{runnableId}/runs/{runId}/params")
	@Override
	public @ResponseBody RunParams getRunParams(@PathVariable String runnableId, @PathVariable long runId) {
		checkIsKnownRun(runnableId, runId);
		return new RunParams(newRun(runnableId, runId), runnableId, runId);
	}

	@GetMapping("/{runnableId}/runs/{runId}/result")
	@Override
	public @ResponseBody RunResult getRunResult(@PathVariable String runnableId, @PathVariable long runId) {
		checkIsKnownRun(runnableId, runId);
		return new RunResult(newRun(runnableId, runId), runnableId, runId);
	}

	@GetMapping("/{runnableId}/runs/{runId}/status")
	@Override
	public @ResponseBody RunStatus getRunStatus(@PathVariable String runnableId, @PathVariable long runId) {
		checkIsKnownRun(runnableId, runId);
		return new RunStatus(newRun(runnableId, runId), runnableId, runId);
	}

	private void checkIsKnownRunnable(String runnableId) {
		if (!getAllIds().contains(runnableId)) {
			throw new UnknownRunnableException(runnableType, runnableId);
		}
	}

	private void checkIsKnownRun(String runnableId, long runId) {
		checkIsKnownRunnable(runnableId);
		if (!getAllRuns(runnableId).contains(runId)) {
			throw new UnknownRunException(runnableId, runId);
		}
	}

	private Run newRun(String runnableId, long runId) {
		return new Run(createRunnable(runnableId), runnableId, runnableRel, getClass(), runId);
	}

}
