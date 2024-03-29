package ar.com.intrale.exceptions;

import ar.com.intrale.messages.Error;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.micronaut.http.HttpStatus;

public class UnauthorizeExeption extends FunctionException {

	public UnauthorizeExeption(Error error, ObjectMapper objectMapper) {
		super(error, objectMapper);
	}

	@Override
	protected HttpStatus getHttpStatus() {
		return HttpStatus.UNAUTHORIZED;
	}

}
