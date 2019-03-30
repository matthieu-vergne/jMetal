package org.uma.jmetal.service.controller.runnable;

import java.util.Map;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.model.runnable.RunParams;
import org.uma.jmetal.service.model.runnable.RunResult;
import org.uma.jmetal.service.model.runnable.RunStatus;

public interface RunnableController {

	Map<Long, ResourceSupport> getRuns(String runnableId);

	Run getRun(String runnableId, long runId);

	RunParams getRunParams(String runnableId, long runId);

	RunResult getRunResult(String runnableId, long runId);

	RunStatus getRunStatus(String runnableId, long runId);

}