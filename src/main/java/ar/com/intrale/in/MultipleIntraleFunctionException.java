package ar.com.intrale.in;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MultipleIntraleFunctionException extends IntraleFunctionException {
	
	private HttpStatus status;
	
	private Collection<IntraleFunctionException> exceptions = new ArrayList<IntraleFunctionException>();
	
	MultipleIntraleFunctionException(HttpStatus status){
		super();
		this.status = status;
	}
	
	public void add(IntraleFunctionException exception) {
		this.exceptions.add(exception);
	}

	@Override
	public Collection<Error> getErrors() {
		Collection<Error> errors = new ArrayList<Error>();
		Iterator<IntraleFunctionException> it = this.exceptions.iterator();
		while (it.hasNext()) {
			IntraleFunctionException intraleFunctionException = (IntraleFunctionException) it.next();
			errors.addAll(intraleFunctionException.getErrors());
		}
		return errors;
	}

	@Override
	public ResponseEntity<String> getResponseEntity() {
		try {
			return new ResponseEntity<String>(mapper.writeValueAsString(getErrors()), status);
		} catch (JsonProcessingException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
