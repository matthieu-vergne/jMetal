package org.uma.jmetal.service.controller.runnable;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RunNotDoneException extends RuntimeException {

	public RunNotDoneException(String runnableId, long runId) {
		super("Run x of y is not done yet");
	}

}
