package ar.com.intrale.cloud;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public interface BuilderForLambda <SOURCE, TARGET> extends Builder<SOURCE, TARGET> {

	public APIGatewayProxyResponseEvent wrapForLambda(TARGET target);
	
}
