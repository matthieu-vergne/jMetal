package org.uma.jmetal.service.model;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

public class RunStatus extends ResourceSupport {

	// TODO factor
	public static final String REL_RUN = "run";

	public final String description;

	public RunStatus(String algoId, long runId) {
		this.description = "Here should appear the status of run " + runId + " of " + algoId + ".";
		Run run = new Run(algoId, runId);
		add(run.getLink(Run.REL_RUN_STATUS).withRel(Link.REL_SELF));
		add(run.getLink(Link.REL_SELF).withRel(REL_RUN));
	}

}
