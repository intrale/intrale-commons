package ar.com.intrale.cloud.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.com.intrale.cloud.Error;
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
