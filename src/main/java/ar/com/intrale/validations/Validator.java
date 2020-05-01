package ar.com.intrale.validations;

import ar.com.intrale.in.Error;

public abstract class Validator {
	
	protected String reference;
	protected String value;
	
	public Validator(String reference, String value) {
		this.reference = reference;
		this.value = value;
	}
	
	public abstract Error validate();
}
