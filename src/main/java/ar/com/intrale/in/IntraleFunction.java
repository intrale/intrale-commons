package ar.com.intrale.in;

import java.lang.reflect.ParameterizedType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.com.intrale.annotations.IOLogger;

public abstract class IntraleFunction <REQ extends Request, RES extends Response> {
	
	private static final String UNEXPECTED = "UNEXPECTED";

	private static final Logger LOGGER = LoggerFactory.getLogger(IntraleFunction.class);
	
	public static final String NAME = "INTRALE_FUNCTION";
	
	private final Class<REQ> requestType = (Class<REQ>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private Authorizer authorizer;
	
	@Autowired
	private ApplicationContext applicationContext;

	@IOLogger
	public ResponseEntity<String> execute (String authorization, String request) {
		LOGGER.debug("Iniciando ejecucion IntraleFunction");
		try {
			authorizer.validate(authorization);
			
			REQ requestObject = (REQ) objectMapper.readValue(request, requestType);

			if (requestObject!=null) {
				requestObject.initialize(applicationContext);
				requestObject.validate();
				return new ResponseEntity<String>(objectMapper.writeValueAsString(function(requestObject)), HttpStatus.OK);
			} else {
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
		} catch (IntraleFunctionException e) {
			return e.getResponseEntity();		
		} catch (Exception e) {
			return getResponseEntity(e);
		} finally {
			LOGGER.debug("Fin ejecucion IntraleFunction");
		}
	}
	
	protected abstract RES function(REQ request)  throws BeansException, IntraleFunctionException ;
	
	public IntraleFunctionException throwException(HttpStatus status, String errorCode, String errorDescription) throws BeansException, IntraleFunctionException {
		throw (IntraleFunctionException) applicationContext.getBean(IntraleFunctionException.NAME, status, new Error(errorCode, errorDescription));
	}
	
	public ResponseEntity<String> getResponseEntity(Exception exception) {
		try {
			throwException(HttpStatus.INTERNAL_SERVER_ERROR, UNEXPECTED, IntraleFunctionException.getStackTrace(exception));
		} catch (BeansException e) {
			return new ResponseEntity<String>(IntraleFunctionException.getStackTrace(e), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IntraleFunctionException e) {
			return e.getResponseEntity();
		}
		return null;
	}
}
