package org.uma.jmetal.service.controller.runnable;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class UnknownRunnableException extends IllegalArgumentException {

	public UnknownRunnableException(String runnableType, String runnableId) {
		super("Unknown " + runnableType + " " + runnableId);
	}

}
