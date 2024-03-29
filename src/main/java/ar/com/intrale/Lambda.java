package ar.com.intrale;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestHandler;

@Introspected
public class Lambda extends MicronautRequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(Lambda.class);
	
	protected BaseFunction function;
   	
	@Inject
	private FunctionBuilder builder;
	
	@PostConstruct
	public void postConstruct() {
		LOGGER.debug("postConstruct");
	}
	
	@Override
    public APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent request) {
		LOGGER.info("Ejecutando lambda");
		
		LOGGER.info("HTTP Method:" + request.getHttpMethod());
		if ("OPTIONS".equals(request.getHttpMethod())){
			LOGGER.info("Retorno para metodo OPTIONS");
			APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
			Map<String, String> responseHeaders = new HashMap<String, String>();
			responseHeaders.put(FunctionConst.ACCESS_CONTROL_ALLOW_ORIGIN, FunctionConst.ALL);
			responseHeaders.put(FunctionConst.ACCESS_CONTROL_ALLOW_METHODS, FunctionConst.GET_OPTIONS_HEAD_PUT_POST);
			responseHeaders.put(FunctionConst.ACCESS_CONTROL_ALLOW_HEADERS, FunctionConst.ALLOW_HEADERS_AVAIABLES);
			responseEvent.setHeaders(responseHeaders); 
	    	responseEvent.setIsBase64Encoded(Boolean.TRUE);
	    	return responseEvent;
		}
		

		LOGGER.info("INTRALE: Normalizando headers keys");
		Map<String, String> headers = BaseFunction.normalizeHeaders(request.getHeaders());

		LOGGER.info("Obteniendo funcion");
		function = builder.getfunction(headers);
		
		LOGGER.info("Llamando a funcion");
    	return function.lambdaApply(headers, request.getPathParameters(), request.getBody());
    }  

	public BaseFunction getFunction() {
		return function;
	}

	
}
