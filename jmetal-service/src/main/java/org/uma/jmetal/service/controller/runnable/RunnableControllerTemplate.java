package org.uma.jmetal.service.controller.runnable;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uma.jmetal.service.Link;
import org.uma.jmetal.service.controller.ControllerTemplate;
import org.uma.jmetal.service.controller.UnknownResourceException;
import org.uma.jmetal.service.model.runnable.ParamsDefinition;
import org.uma.jmetal.service.model.runnable.ParamsExample;
import org.uma.jmetal.service.model.runnable.ResultDefinition;
import org.uma.jmetal.service.model.runnable.ResultExample;
import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.model.runnable.RunParams;
import org.uma.jmetal.service.model.runnable.RunResult;
import org.uma.jmetal.service.model.runnable.RunStatus;
import org.uma.jmetal.service.register.run.RunRegisterSupplier;

public abstract class RunnableControllerTemplate<T extends ResourceSupport> extends ControllerTemplate<T> implements RunnableController {

	private final String runnableRel;
	private final String runnableType;
	private final RunRegisterSupplier runRegisterSupplier;

	public RunnableControllerTemplate(String runnableType, String runnableRel,
			RunRegisterSupplier runRegisterSupplier) {
		super(runnableType);
		this.runnableRel = runnableRel;
		this.runnableType = runnableType;
		this.runRegisterSupplier = runRegisterSupplier;
	}

	protected abstract T createRunnable(String runnableId);

	@Override
	protected T createResource(String resourceId) {
		return createRunnable(resourceId);
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
		Map<Long, ResourceSupport> runs = getRunIds(runnableId).stream().collect(Collectors.toMap(id -> id, id -> {
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
			throw new UnknownResourceException(runnableType, runnableId);
		}
	}

	private void checkIsKnownRun(String runnableId, long runId) {
		checkIsKnownRunnable(runnableId);
		if (!getRunIds(runnableId).contains(runId)) {
			throw new UnknownRunException(runnableId, runId);
		}
	}

	private Run newRun(String runnableId, long runId) {
		return new Run(createRunnable(runnableId), runnableId, runnableRel, getClass(), runId);
	}

	private Collection<Long> getRunIds(String runnableId) {
		return runRegisterSupplier.get(runnableType, runnableId).getIds();
	}

}
