package org.uma.jmetal.service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class UnknownAlgorithmException extends IllegalArgumentException {

	public UnknownAlgorithmException(String algorithmId) {
		super("Unknown algorithm: " + algorithmId);
	}

}
