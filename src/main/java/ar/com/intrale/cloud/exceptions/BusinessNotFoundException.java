package ar.com.intrale.cloud.exceptions;

import java.util.Collection;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.com.intrale.cloud.Error;
import io.micronaut.http.HttpStatus;

public class BusinessNotFoundException extends FunctionException {

	public BusinessNotFoundException(Error error, ObjectMapper objectMapper) {
		super(error, objectMapper);
	}

	public BusinessNotFoundException(Collection<Error> errors, ObjectMapper objectMapper) {
		super(errors, objectMapper);
	}

	@Override
	protected HttpStatus getHttpStatus() {
		return HttpStatus.NOT_FOUND;
	}

}
