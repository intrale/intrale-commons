package ar.com.intrale.in;

import org.springframework.http.ResponseEntity;

public class AuthorizationResult {

	private Boolean authorized;
	private ResponseEntity<String> responseEntity;
	
	public ResponseEntity<String> getResponseEntity() {
		return responseEntity;
	}

	public void setResponseEntity(ResponseEntity<String> responseEntity) {
		this.responseEntity = responseEntity;
	}

	public AuthorizationResult(Boolean authorized, ResponseEntity<String> responseEntity) {
		this.authorized = authorized;
		this.responseEntity = responseEntity;
	}
	
	public Boolean getAuthorized() {
		return authorized;
	}
	public void setAuthorized(Boolean authorized) {
		this.authorized = authorized;
	}
	
}
