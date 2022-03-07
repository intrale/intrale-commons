package ar.com.intrale.cloud;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "requestRoot", "requestId" })
public class RequestChild implements Request {
	
	private RequestRoot requestRoot;

	public RequestRoot getRequestRoot() {
		return requestRoot;
	}

	public void setRequestRoot(RequestRoot requestRoot) {
		this.requestRoot = requestRoot;
	}

	@Override
	public String getRequestId() {
		return requestRoot.getRequestId();
	}

}
