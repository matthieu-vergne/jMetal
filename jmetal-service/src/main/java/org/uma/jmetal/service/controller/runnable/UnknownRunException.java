package org.uma.jmetal.service.controller.runnable;

@SuppressWarnings("serial")
public class UnknownRunException extends IllegalArgumentException {

	public UnknownRunException(String parentId, long runId) {
		super("Unknown run " + runId + " for " + parentId);
	}

}
