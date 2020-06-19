package ar.com.intrale.in;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import ar.com.intrale.validations.Validator;

public abstract class Request {
	
	public abstract Collection<Error> validate();

	protected Collection<Error> validate(Collection<Validator> validations) {
		Collection<Error> errors = new ArrayList<Error>();
		Iterator<Validator> it = validations.iterator();
		while (it.hasNext()) {
			Validator validation = (Validator) it.next();
			Error error = validation.validate();
			if(error!=null) {
				errors.add(error);
			}
		}
		
		//FIXME: Aca deberiamos lanzar el error badrequest en caso de que corresponda
		
		return errors;
	}
	
}
