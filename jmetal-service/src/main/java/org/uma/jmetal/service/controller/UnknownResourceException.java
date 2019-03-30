package org.uma.jmetal.service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class UnknownResourceException extends IllegalArgumentException {

	public UnknownResourceException(String runnableType, String runnableId) {
		super("Unknown " + runnableType + " " + runnableId);
	}

}
