package org.uma.jmetal.service.model.runnable;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Rel;

public class RunResult {

	public static class Response extends ResourceSupport {

		public final String description;

		public Response(Run.Response run, String algoId, long runId) {
			this.description = "Here should appear the result of run " + runId + " of " + algoId + ".";
			add(run.getLink(Rel.RUN_RESULT).withRel(Rel.SELF));
			add(run.getLink(Rel.SELF).withRel(Rel.RUN));
		}

	}

}
