package org.uma.jmetal.service.controller.runnable;

import java.util.Map;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.model.runnable.RunsStats;

public interface RunnableController {

	Map<Long, ResourceSupport> getRuns(String runnableId);

	RunsStats.Response getRunsStats(String runnableId);

	Run.Response getRun(String runnableId, long runId);

	Run.Params.Response getRunParams(String runnableId, long runId);

	Run.Result.Response getRunResult(String runnableId, long runId);

	Run.Status.Response getRunStatus(String runnableId, long runId);

}
