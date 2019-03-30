package org.uma.jmetal.service.model.runnable;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Link;

public class RunResult extends ResourceSupport {

	public final String description;

	public RunResult(Run run, String algoId, long runId) {
		this.description = "Here should appear the result of run " + runId + " of " + algoId + ".";
		add(run.getLink(Link.REL_RUN_RESULT).withRel(Link.REL_SELF));
		add(run.getLink(Link.REL_SELF).withRel(Link.REL_RUN));
	}

}
