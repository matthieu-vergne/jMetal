package org.uma.jmetal.service.controller;

@SuppressWarnings("serial")
public class UnknownAlgorithmRunException extends IllegalArgumentException {

	public UnknownAlgorithmRunException(String algorithmId, long runId) {
		super("Unknown " + algorithmId + " run:" + runId);
	}

}
