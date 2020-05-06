package ar.com.intrale.in;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class IntraleFunction <REQ extends Request, RES extends Response> {
	
	public static final String NAME = "INTRALE_FUNCTION";
	
	private final Class<REQ> requestType = (Class<REQ>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	private final Class<RES> responseType = (Class<RES>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
	
	@Autowired
	private JSONUtils utils;
	
	@Autowired
	private Authorizer authorizer;

	public String execute (String authorization, String request) {
		RES responseObject = null;
		try {
			AuthorizationResult result = authorizer.validate("", authorization);
			if (result.getAuthorized()) {
				REQ requestObject = (REQ) utils.toObject(request, requestType);
				Collection<Error> errors = requestObject.validate();
				if ((errors!=null) && (errors.size()>0)) {
					responseObject = responseType.newInstance();
					responseObject.setErrors(errors);
				} else {
					responseObject = function(requestObject);
				}
			} else {
				return  utils.toString(result.getAuthorizationResponse());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return utils.toString(new UnexpectedErrorResponse());
		}
		return utils.toString(responseObject);
		
	}
	
	protected abstract RES function(REQ request);
}
