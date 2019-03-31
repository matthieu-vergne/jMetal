package org.uma.jmetal.service.controller.runnable;

import java.util.Map;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.model.runnable.Run;

public interface RunnableController {

	Map<Long, ResourceSupport> getRuns(String runnableId);

	Run.Response getRun(String runnableId, long runId);

	Run.Params.Response getRunParams(String runnableId, long runId);

	Run.Result.Response getRunResult(String runnableId, long runId);

	Run.Status.Response getRunStatus(String runnableId, long runId);

}
