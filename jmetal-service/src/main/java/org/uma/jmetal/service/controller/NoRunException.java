package org.uma.jmetal.service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NoRunException extends IllegalStateException {

	public NoRunException(String algorithmId) {
		super("No run found for " + algorithmId);
	}

}
