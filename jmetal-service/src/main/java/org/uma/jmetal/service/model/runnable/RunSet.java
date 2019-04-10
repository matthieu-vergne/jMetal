package org.uma.jmetal.service.model.runnable;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.Collection;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Rel;
import org.uma.jmetal.service.controller.runnable.RunnableController;

public class RunSet {

	public static class Response extends ResourceSupport {

		public final long count;

		public Response(Collection<Long> runIds, ResourceSupport parent, String parentId, String parentRel,
				Class<? extends RunnableController> parentController) {
			this.count = runIds.size();

			add(linkTo(methodOn(parentController).getRuns(parentId)).withSelfRel());
			if (!runIds.isEmpty()) {
				long firstId = runIds.stream().mapToLong(l -> l).min().getAsLong();
				long lastId = runIds.stream().mapToLong(l -> l).max().getAsLong();
				add(linkTo(methodOn(parentController).getRun(parentId, firstId)).withRel(Rel.FIRST));
				add(linkTo(methodOn(parentController).getRun(parentId, lastId)).withRel(Rel.LAST));
			}
			add(parent.getLink(Rel.SELF).withRel(parentRel));
		}

	}

}
