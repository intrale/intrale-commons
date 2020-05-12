package ar.com.intrale.in;

import java.util.ArrayList;
import java.util.Collection;

public class NullRequestObjectErrorResponse extends Response {
	public NullRequestObjectErrorResponse() {
		Collection<Error> errors = new ArrayList<Error>();
		errors.add(new Error("NULL_REQUEST", "Null Request Object"));
		this.setErrors(errors);
	}
}
