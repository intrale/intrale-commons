package ar.com.intrale.in;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IntraleFunctionException extends Exception {

	protected ObjectMapper mapper = new ObjectMapper();
	
	private HttpStatus status;
	private Collection<Error> errors = new ArrayList<Error>();
	
	public Collection<Error> getErrors() {
		return errors;
	}

	public ResponseEntity<String> getResponseEntity() {
		try {
			return new ResponseEntity<String>(mapper.writeValueAsString(errors), status);
		} catch (JsonProcessingException e) {
			return new ResponseEntity<String>(IntraleFunctionException.getStackTrace(e), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public IntraleFunctionException(){}
	
	public IntraleFunctionException(HttpStatus status, Collection<Error> errors){
		if (status!=null) {
			this.status = status;
		}
		if (errors!=null) {
			this.errors = errors;
		}
	}
	
	public IntraleFunctionException(HttpStatus status, Error error){
		if (status!=null) {
			this.status = status;
		}
		if (error!=null) {
			this.errors.add(error);
		}
	}
	
	public IntraleFunctionException(HttpStatus status, String errorCode){
		if (status!=null) {
			this.status = status;
		}
		if (errorCode!=null) {
			this.errors.add(new Error(errorCode, ""));
		}
	}

	public static String getStackTrace(Throwable e) {
		StringWriter sWriter = new StringWriter();
		PrintWriter pilaMensajes = new PrintWriter(sWriter);
		e.printStackTrace(pilaMensajes);
		String stackTrace = sWriter.toString();
		return stackTrace;
	}
}
