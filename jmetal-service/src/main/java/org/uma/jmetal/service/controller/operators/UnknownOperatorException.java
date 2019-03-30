package org.uma.jmetal.service.controller.operators;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class UnknownOperatorException extends IllegalArgumentException {

	public UnknownOperatorException(String operatorId) {
		super("Unknown operator " + operatorId);
	}

}
