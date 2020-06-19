package ar.com.intrale.in;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class IntraleFunction <REQ extends Request, RES extends Response> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IntraleFunction.class);
	
	public static final String NAME = "INTRALE_FUNCTION";
	
	private final Class<REQ> requestType = (Class<REQ>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	private final Class<RES> responseType = (Class<RES>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
	
	@Autowired
	private JSONUtils utils;
	
	@Autowired
	private Authorizer authorizer;

	public ResponseEntity<String> execute (String authorization, String request) {
		LOGGER.debug("Iniciando ejecucion IntraleFunction");
		RES responseObject = null;
		try {
			AuthorizationResult result = authorizer.validate("", authorization);
			if (result.getAuthorized()) {
				REQ requestObject = (REQ) utils.toObject(request, requestType);
				
				if (requestObject!=null) {
					Collection<Error> errors = requestObject.validate();
					
					if ((errors!=null) && (errors.size()>0)) {
						responseObject = responseType.newInstance();
						responseObject.setErrors(errors);
						return new ResponseEntity<String>(utils.toString(responseObject), HttpStatus.BAD_REQUEST);	
					} else {
						responseObject = function(requestObject);
						return new ResponseEntity<String>(utils.toString(responseObject), HttpStatus.OK);
					}
					
				} else {
					return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
				}
				
			} else {
				LOGGER.info("No se encuentra autorizado para ejecutar:" + authorization);
				return result.getResponseEntity();
			}
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			LOGGER.debug("Fin ejecucion IntraleFunction");
		}
	}
	
	protected abstract RES function(REQ request);
}
