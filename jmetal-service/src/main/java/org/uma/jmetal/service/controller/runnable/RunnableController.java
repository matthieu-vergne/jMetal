package org.uma.jmetal.service.controller.runnable;

import org.uma.jmetal.service.model.runnable.ParamsDefinition;
import org.uma.jmetal.service.model.runnable.ParamsExample;
import org.uma.jmetal.service.model.runnable.ResultDefinition;
import org.uma.jmetal.service.model.runnable.ResultExample;
import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.model.runnable.RunSet;
import org.uma.jmetal.service.model.runnable.RunsStats;

public interface RunnableController {

	ParamsDefinition.Response getParamsDefinition(String runnableId);

	ParamsExample.Response getParamsExample(String runnableId);

	ResultDefinition.Response getResultDefinition(String runnableId);

	ResultExample.Response getResultExample(String runnableId);

	RunSet.Response getRuns(String runnableId);

	RunsStats.Response getRunsStats(String runnableId);

	Run.Response getRun(String runnableId, long runId);

	Run.Params.Response getRunParams(String runnableId, long runId);

	Run.Result.Response getRunResult(String runnableId, long runId);

	Run.Status.Response getRunStatus(String runnableId, long runId);

}
