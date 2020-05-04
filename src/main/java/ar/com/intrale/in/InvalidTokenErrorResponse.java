package ar.com.intrale.in;

import java.util.ArrayList;
import java.util.Collection;

public class InvalidTokenErrorResponse extends Response {
	public InvalidTokenErrorResponse() {
		Collection<Error> errors = new ArrayList<Error>();
		errors.add(new Error("INVALID_TOKEN", "Invalid Token"));
		this.setErrors(errors);
	}
}
