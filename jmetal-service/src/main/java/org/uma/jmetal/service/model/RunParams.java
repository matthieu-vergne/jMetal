package org.uma.jmetal.service.model;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

public class RunParams extends ResourceSupport {

	// TODO factor
	public static final String REL_RUN = "run";

	public final String description;

	public RunParams(String algoId, long runId) {
		this.description = "Here should appear the various parameters used for the run " + runId + " of " + algoId
				+ ".";
		Run run = new Run(algoId, runId);
		add(run.getLink(Run.REL_RUN_PARAMS).withRel(Link.REL_SELF));
		add(run.getLink(Link.REL_SELF).withRel(REL_RUN));
	}

}
