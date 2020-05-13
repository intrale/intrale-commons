package ar.com.intrale.in;

import java.util.ArrayList;
import java.util.Collection;

public class InvalidRequestObjectErrorResponse extends Response {
	public InvalidRequestObjectErrorResponse() {
		Collection<Error> errors = new ArrayList<Error>();
		errors.add(new Error("INVALID_REQUEST", "Invalid Request"));
		this.setErrors(errors);
	}
}
