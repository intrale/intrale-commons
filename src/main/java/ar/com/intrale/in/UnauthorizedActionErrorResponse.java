package ar.com.intrale.in;

import java.util.ArrayList;
import java.util.Collection;

public class UnauthorizedActionErrorResponse extends Response {
	public UnauthorizedActionErrorResponse() {
		Collection<Error> errors = new ArrayList<Error>();
		errors.add(new Error("UNAUTHORIZED_ACTION", "Unauthorized action"));
		this.setErrors(errors);
	}
}
