package org.uma.jmetal.service.model.runnable;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Rel;
import org.uma.jmetal.service.controller.runnable.RunnableController;

public class RunnableTemplate {

	public static class Response extends ResourceSupport {

		public Response(String id, RunnableController controller) {
			add(linkTo(controller.getParamsDefinition(id)).withRel(Rel.PARAMS_DEFINITION));
			add(linkTo(controller.getParamsExample(id)).withRel(Rel.PARAMS_EXAMPLE));
			add(linkTo(controller.getResultDefinition(id)).withRel(Rel.RESULT_DEFINITION));
			add(linkTo(controller.getResultExample(id)).withRel(Rel.RESULT_EXAMPLE));
			add(linkTo(controller.getRuns(id)).withRel(Rel.RUNS));
			add(linkTo(controller.getRunsStats(id)).withRel(Rel.RUNS_STATS));
		}

	}

}
