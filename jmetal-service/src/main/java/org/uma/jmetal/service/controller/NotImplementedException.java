package org.uma.jmetal.service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.NOT_IMPLEMENTED)
public class NotImplementedException extends IllegalStateException {

	public NotImplementedException() {
		super("Not implemented yet");
	}
}
