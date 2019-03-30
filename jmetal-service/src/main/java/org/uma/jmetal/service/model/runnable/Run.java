package org.uma.jmetal.service.model.runnable;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Link;
import org.uma.jmetal.service.controller.runnable.RunnableController;

public class Run extends ResourceSupport {

	public final String description;

	public Run(ResourceSupport parent, String parentId, String parentRel, Class<? extends RunnableController> parentController, long runId) {
		this.description = "Here may appear a summary of the run " + runId + " of " + parentId + ".";
		add(linkTo(methodOn(parentController).getRun(parentId, runId)).withSelfRel());
		add(linkTo(methodOn(parentController).getRunParams(parentId, runId)).withRel(Link.REL_RUN_PARAMS));
		add(linkTo(methodOn(parentController).getRunResult(parentId, runId)).withRel(Link.REL_RUN_RESULT));
		add(linkTo(methodOn(parentController).getRunStatus(parentId, runId)).withRel(Link.REL_RUN_STATUS));
		add(parent.getLink(Link.REL_SELF).withRel(parentRel));
	}

}
