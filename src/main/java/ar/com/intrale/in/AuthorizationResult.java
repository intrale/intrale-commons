package ar.com.intrale.in;

public class AuthorizationResult {

	private Boolean authorized;
	private Response authorizationResponse;
	
	public AuthorizationResult(Boolean authorized, Response authorizationResponse) {
		this.authorized = authorized;
		this.authorizationResponse = authorizationResponse;
	}
	
	public Boolean getAuthorized() {
		return authorized;
	}
	public void setAuthorized(Boolean authorized) {
		this.authorized = authorized;
	}
	public Response getAuthorizationResponse() {
		return authorizationResponse;
	}
	public void setAuthorizationResponse(Response authorizationResponse) {
		this.authorizationResponse = authorizationResponse;
	}
	
	
	
}
