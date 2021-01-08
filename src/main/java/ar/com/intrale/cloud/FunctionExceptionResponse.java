package ar.com.intrale.cloud;

import java.util.ArrayList;
import java.util.Collection;

import io.micronaut.core.annotation.Introspected;

//@Introspected
public class FunctionExceptionResponse extends Response {
	
	private Collection<Error> errors = new ArrayList<Error>();
	
	public FunctionExceptionResponse() {}
	
	public FunctionExceptionResponse(Collection<Error> errors) {
		this.errors = errors;
	}

	public Collection<Error> getErrors() {
		return errors;
	}

	public void setErrors(Collection<Error> errors) {
		this.errors = errors;
	}
	
	

}
