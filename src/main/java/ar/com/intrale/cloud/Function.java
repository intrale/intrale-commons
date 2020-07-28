package ar.com.intrale.cloud;

import java.lang.reflect.ParameterizedType;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.validation.validator.Validator;


public abstract class Function<REQ extends Request, RES extends Response> implements java.util.function.Function<String, HttpResponse<String>> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Function.class);
	
	private final Class<Request> requestType = (Class<Request>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	
	@Inject
	protected Config config;

   	@Inject
   	protected Validator validator;
	
   	@Inject
   	protected ObjectMapper objectMapper;
   	
   	@Inject
   	protected RequestBuilder builder;
   	
	@Override
	public HttpResponse<String> apply(String request) {
		
    	LOGGER.info("iniciando funcion");
    	
    	try {
    		Response response = new Response();
    		REQ requestObject = (REQ) builder.build(request, requestType);
	    	
	    	if (StringUtils.isEmpty(request)) {
	    		return HttpResponse.badRequest();
	    	}
	    	
	    	return HttpResponse.ok().body(objectMapper.writeValueAsString(execute(requestObject)));
		} catch (FunctionException e) {
			return e.getResponse();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return HttpResponse.serverError();
        
	}
	
	public abstract RES execute (REQ request) throws FunctionException;

}
