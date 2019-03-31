package org.uma.jmetal.service.model.runnable;

import java.util.Map;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Rel;

public class RunsStats {

	public static class Response extends ResourceSupport {

		public final long pending;
		public final long running;
		public final long done;

		public Response(Map<Run.Status, Long> stats, ResourceSupport parent, String parentRel) {
			this.pending = stats.get(Run.Status.PENDING);
			this.running = stats.get(Run.Status.RUNNING);
			this.done = stats.get(Run.Status.DONE);
			add(parent.getLink(Rel.RUNS_STATS).withRel(Rel.SELF));
			add(parent.getLink(Rel.RUNS));
			add(parent.getLink(Rel.SELF).withRel(parentRel));
		}

	}

}
