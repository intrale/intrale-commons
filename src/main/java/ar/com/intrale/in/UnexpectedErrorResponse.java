package ar.com.intrale.in;

import java.util.ArrayList;
import java.util.Collection;

public class UnexpectedErrorResponse extends Response {

	public UnexpectedErrorResponse() {
		Collection<Error> errors = new ArrayList<Error>();
		errors.add(new Error("UNEXPECTED", "Unexpected error occurred"));
		this.setErrors(errors);
	}
}
