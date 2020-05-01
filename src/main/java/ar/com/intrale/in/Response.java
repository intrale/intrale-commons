package ar.com.intrale.in;

import java.util.ArrayList;
import java.util.Collection;

public abstract class Response {

	private Collection<Error> errors;

	public Collection<Error> getErrors() {
		return errors;
	}

	public void setErrors(Collection<Error> errors) {
		this.errors = errors;
	}
	
	public void addError(String code, String description) {
		if(errors==null) {
			errors = new ArrayList<Error>();
		}
		errors.add(new Error(code, description));
	}
	
}
