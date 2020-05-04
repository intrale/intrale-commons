package ar.com.intrale.in;

import java.util.ArrayList;
import java.util.Collection;

public class NotAuthorizationFoundErrorResponse extends Response {
	public NotAuthorizationFoundErrorResponse() {
		Collection<Error> errors = new ArrayList<Error>();
		errors.add(new Error("NOT_AUTHORIZATION", "Not Authorization Found"));
		this.setErrors(errors);
	}
}
