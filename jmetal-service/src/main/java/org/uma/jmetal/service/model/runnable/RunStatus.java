package org.uma.jmetal.service.model.runnable;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Link;

public class RunStatus extends ResourceSupport {

	public final String description;

	public RunStatus(Run run, String algoId, long runId) {
		this.description = "Here should appear the status of run " + runId + " of " + algoId + ".";
		add(run.getLink(Link.REL_RUN_STATUS).withRel(Link.REL_SELF));
		add(run.getLink(Link.REL_SELF).withRel(Link.REL_RUN));
	}

}
