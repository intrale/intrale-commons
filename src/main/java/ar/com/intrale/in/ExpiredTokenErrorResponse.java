package ar.com.intrale.in;

import java.util.ArrayList;
import java.util.Collection;

public class ExpiredTokenErrorResponse extends Response {
	public ExpiredTokenErrorResponse() {
		Collection<Error> errors = new ArrayList<Error>();
		errors.add(new Error("TOKEN_EXPIRED", "Token Expired"));
		this.setErrors(errors);
	}
}
