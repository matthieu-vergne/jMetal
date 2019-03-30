package org.uma.jmetal.service.controller.runnable;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NoRunException extends IllegalStateException {

	public NoRunException(String runnableId) {
		super("No run found for " + runnableId);
	}

}
