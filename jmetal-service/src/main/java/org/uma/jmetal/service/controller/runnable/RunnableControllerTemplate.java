package org.uma.jmetal.service.controller.runnable;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uma.jmetal.service.Rel;
import org.uma.jmetal.service.controller.ControllerTemplate;
import org.uma.jmetal.service.controller.UnknownResourceException;
import org.uma.jmetal.service.executor.RunExecutor;
import org.uma.jmetal.service.model.runnable.ParamsDefinition;
import org.uma.jmetal.service.model.runnable.ParamsExample;
import org.uma.jmetal.service.model.runnable.ResultDefinition;
import org.uma.jmetal.service.model.runnable.ResultExample;
import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.model.runnable.Run.Status;
import org.uma.jmetal.service.model.runnable.RunSet;
import org.uma.jmetal.service.model.runnable.RunsStats;
import org.uma.jmetal.service.register.run.RunRegister;
import org.uma.jmetal.service.register.run.RunRegisterSupplier;

public abstract class RunnableControllerTemplate<RunnableResponse extends ResourceSupport>
		extends ControllerTemplate<RunnableResponse> implements RunnableController {

	private final String runnableRel;
	private final String runnableType;
	private final RunRegisterSupplier runRegisterSupplier;
	private final Map<String, RunRegister> runRegisters = new HashMap<>();
	private final RunExecutor executor;

	public RunnableControllerTemplate(String runnableType, String runnableRel, RunRegisterSupplier runRegisterSupplier,
			RunExecutor executor) {
		super(runnableType);
		this.runnableRel = runnableRel;
		this.runnableType = runnableType;
		this.runRegisterSupplier = runRegisterSupplier;
		this.executor = executor;
	}

	protected abstract RunnableResponse createRunnableResponse(String runnableId);

	protected abstract ParamsDefinition getRunnableParamsDefinition(String runnableId);

	protected abstract Function<Run.Params, Object> getRunnableFunction(String runnableId);

	protected abstract ResultDefinition<?> getRunnableResultDefinition(String runnableId);

	@Override
	protected RunnableResponse createResourceResponse(String resourceId) {
		return createRunnableResponse(resourceId);
	}

	@GetMapping("/{runnableId}/params/definition")
	@Override
	public ParamsDefinition.Response getParamsDefinition(@PathVariable String runnableId) {
		checkIsKnownRunnable(runnableId);
		ParamsDefinition def = getRunnableParamsDefinition(runnableId);
		return new ParamsDefinition.Response(def, createRunnableResponse(runnableId), runnableId, runnableRel);
	}

	@GetMapping("/{runnableId}/params/example")
	@Override
	public ParamsExample.Response getParamsExample(@PathVariable String runnableId) {
		checkIsKnownRunnable(runnableId);
		ParamsExample example = getRunnableParamsDefinition(runnableId).createExample();
		return new ParamsExample.Response(example, createRunnableResponse(runnableId), runnableId, runnableRel);
	}

	@GetMapping("/{runnableId}/result/definition")
	@Override
	public ResultDefinition.Response getResultDefinition(@PathVariable String runnableId) {
		checkIsKnownRunnable(runnableId);
		ResultDefinition<?> def = getRunnableResultDefinition(runnableId);
		return new ResultDefinition.Response(def, createRunnableResponse(runnableId), runnableId, runnableRel);
	}

	@GetMapping("/{runnableId}/result/example")
	@Override
	public ResultExample.Response getResultExample(@PathVariable String runnableId) {
		checkIsKnownRunnable(runnableId);
		Object example = getRunnableResultDefinition(runnableId).createExample();
		return new ResultExample.Response(example, createRunnableResponse(runnableId), runnableId, runnableRel);
	}

	@GetMapping("/{runnableId}/runs")
	@Override
	public @ResponseBody RunSet.Response getRuns(@PathVariable String runnableId) {
		checkIsKnownRunnable(runnableId);
		Map<Long, ResourceSupport> runs = getRunIds(runnableId).stream().collect(Collectors.toMap(id -> id, id -> {
			ResourceSupport resource = new ResourceSupport();
			resource.add(newRunResponse(runnableId, id).getLink(Rel.SELF));
			resource.add(linkTo(methodOn(getClass()).getRunsStats(runnableId)).withRel(Rel.RUNS_STATS));
			return resource;
		}));
		return new RunSet.Response(runs, createRunnableResponse(runnableId), runnableId, runnableRel, getClass());
	}

	@PostMapping(path = "/{runnableId}/runs")
	public @ResponseBody Run.Response addRun(@PathVariable String runnableId, @RequestBody Run.Request request) {
		checkIsKnownRunnable(runnableId);

		RunRegister runRegister = getRunRegister(runnableId);
		Function<Run.Params, Object> function = getRunnableFunction(runnableId);
		long runId = runRegister.store(request, function);

		executor.submit(runRegister.retrieve(runId));

		return newRunResponse(runnableId, runId);
	}

	@GetMapping("/{runnableId}/runs/stats")
	@Override
	public @ResponseBody RunsStats.Response getRunsStats(@PathVariable String runnableId) {
		checkIsKnownRunnable(runnableId);
		RunRegister register = getRunRegister(runnableId);
		Map<Status, Long> statuses = new LinkedHashMap<>();
		statuses.put(Status.PENDING, 0L);
		statuses.put(Status.RUNNING, 0L);
		statuses.put(Status.DONE, 0L);
		getRunIds(runnableId).stream().map(id -> register.retrieve(id).getStatus())
				.forEach(status -> statuses.put(status, statuses.get(status) + 1));
		return new RunsStats.Response(statuses, createRunnableResponse(runnableId), runnableRel);
	}

	@GetMapping("/{runnableId}/runs/{runId}")
	@Override
	public @ResponseBody Run.Response getRun(@PathVariable String runnableId, @PathVariable long runId) {
		checkIsKnownRun(runnableId, runId);
		return newRunResponse(runnableId, runId);
	}

	@GetMapping("/{runnableId}/runs/{runId}/params")
	@Override
	public @ResponseBody Run.Params.Response getRunParams(@PathVariable String runnableId, @PathVariable long runId) {
		checkIsKnownRun(runnableId, runId);
		Run.Params runParams = runRegisters.get(runnableId).retrieve(runId).getParams();
		Run.Response runResponse = newRunResponse(runnableId, runId);
		return new Run.Params.Response(runParams, runResponse);
	}

	@GetMapping("/{runnableId}/runs/{runId}/result")
	@Override
	public @ResponseBody Run.Result.Response getRunResult(@PathVariable String runnableId, @PathVariable long runId) {
		checkIsKnownRun(runnableId, runId);
		Run run = runRegisters.get(runnableId).retrieve(runId);
		if (run.getStatus() == Run.Status.DONE) {
			Run.Result runResult = run.getResult();
			Run.Response runResponse = newRunResponse(runnableId, runId);
			return new Run.Result.Response(runResult, runResponse);
		} else {
			throw new RunNotDoneException(runnableId, runId);
		}
	}

	@GetMapping("/{runnableId}/runs/{runId}/status")
	@Override
	public @ResponseBody Run.Status.Response getRunStatus(@PathVariable String runnableId, @PathVariable long runId) {
		checkIsKnownRun(runnableId, runId);
		Run.Status runStatus = runRegisters.get(runnableId).retrieve(runId).getStatus();
		Run.Response runResponse = newRunResponse(runnableId, runId);
		return new Run.Status.Response(runStatus, runResponse);
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

	private Run.Response newRunResponse(String runnableId, long runId) {
		Run run = getRunRegister(runnableId).retrieve(runId);
		RunnableResponse parentResponse = createRunnableResponse(runnableId);
		return new Run.Response(run, parentResponse, runnableId, runnableRel, getClass(), runId);
	}

	private RunRegister getRunRegister(String runnableId) {
		RunRegister register = runRegisters.get(runnableId);
		if (register == null) {
			register = runRegisterSupplier.get(runnableType, runnableId);
			runRegisters.put(runnableId, register);
		} else {
			// reuse it
		}
		return register;
	}

	private Collection<Long> getRunIds(String runnableId) {
		return getRunRegister(runnableId).getIds();
	}

}
