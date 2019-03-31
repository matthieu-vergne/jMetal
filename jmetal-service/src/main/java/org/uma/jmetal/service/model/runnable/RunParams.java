package org.uma.jmetal.service.model.runnable;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Rel;

public class RunParams extends ResourceSupport {

	public final String description;

	public RunParams(Run run, String algoId, long runId) {
		this.description = "Here should appear the various parameters used for the run " + runId + " of " + algoId
				+ ".";
		add(run.getLink(Rel.RUN_PARAMS).withRel(Rel.SELF));
		add(run.getLink(Rel.SELF).withRel(Rel.RUN));
	}

}
