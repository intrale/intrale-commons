package ar.com.intrale.in;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.BeansException;
import org.springframework.http.HttpStatus;

import ar.com.intrale.validations.Validator;

public abstract class Request {
	
	private Collection<Validator> validations = new ArrayList<Validator>();
	private MultipleIntraleFunctionException exception;

	protected void addValidator(Validator validator) {
		validations.add(validator);
	}
	
	public void validate() throws MultipleIntraleFunctionException {
		Iterator<Validator> it = validations.iterator();
		
		while (it.hasNext()) {
			Validator validator = (Validator) it.next();
			try {
				validator.validate();
			} catch (BeansException e) {
				initializeMultipleExceptions();
				exception.add(new IntraleFunctionException (HttpStatus.INTERNAL_SERVER_ERROR, IntraleFunctionException.getStackTrace(e)));
			} catch (IntraleFunctionException e) {
				initializeMultipleExceptions();
				exception.add(e);
			}
		}
		
		if (exception!=null) {
			throw exception;
		}
	}
	
	private void initializeMultipleExceptions() {
		if (exception==null) {
			exception = new MultipleIntraleFunctionException(HttpStatus.BAD_REQUEST);
		}
	}
	
}
