package org.uma.jmetal.service.model.runnable;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.Map;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Rel;
import org.uma.jmetal.service.controller.runnable.RunnableController;

public class RunSet {

	public static class Response extends ResourceSupport {

		public final Map<Long, ResourceSupport> runs;

		public Response(Map<Long, ResourceSupport> runs, ResourceSupport parent, String parentId, String parentRel,
				Class<? extends RunnableController> parentController) {
			this.runs = runs;
			add(linkTo(methodOn(parentController).getRuns(parentId)).withSelfRel());
			add(parent.getLink(Rel.SELF).withRel(parentRel));
		}

	}

}
