package ar.com.intrale.exceptions;

import java.util.Collection;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.com.intrale.messages.Error;
import io.micronaut.http.HttpStatus;

public class NotFoundException extends FunctionException {

	public NotFoundException(Error error, ObjectMapper objectMapper) {
		super(error, objectMapper);
	}

	public NotFoundException(Collection<Error> errors, ObjectMapper objectMapper) {
		super(errors, objectMapper);
	}


	@Override
	protected HttpStatus getHttpStatus() {
		return HttpStatus.NOT_FOUND;
	}

}
