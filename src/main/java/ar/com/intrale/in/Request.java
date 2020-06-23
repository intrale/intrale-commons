package ar.com.intrale.in;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;

import ar.com.intrale.validations.Validator;

public abstract class Request {
	
	private Collection<Validator> validations = new ArrayList<Validator>();
	protected ApplicationContext applicationContext;
	private MultipleIntraleFunctionException exception;

	public void initialize(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		initializeValidators();
	}
	
	protected abstract void initializeValidators();

	protected void addValidator(Validator validator) {
		validations.add(validator);
	}
	
	protected void addValidator(Class validator, String reference) {
		validations.add((Validator) applicationContext.getBean(validator, reference, this));
	}
	
	public void validate() throws MultipleIntraleFunctionException {
		Iterator<Validator> it = validations.iterator();
		
		while (it.hasNext()) {
			Validator validator = (Validator) it.next();
			try {
				validator.validate();
			} catch (BeansException e) {
				initializeMultipleExceptions();
				exception.add((IntraleFunctionException) applicationContext.getBean(IntraleFunctionException.NAME, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
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
			exception = applicationContext.getBean(MultipleIntraleFunctionException.class, HttpStatus.BAD_REQUEST);
		}
	}
	
}
