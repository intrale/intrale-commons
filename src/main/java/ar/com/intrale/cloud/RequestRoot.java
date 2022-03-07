package ar.com.intrale.cloud;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.core.util.StringUtils;

@JsonIgnoreProperties(value = { "headers", "queryStringParameters" })
public class RequestRoot implements Request{
	
	private Map <String, String> headers;
	
	private Map <String, String> pathParameters;

	@NonNull
    @NotBlank
    private String requestId;

	@NonNull
    @NotBlank
    public String getRequestId() {
		return requestId;
	}

	public void setRequestId(@NonNull String requestId) {
		this.requestId = requestId;
	}
	
	public Map<String, String> getPathParameters() {
		return pathParameters;
	}

	public void setPathParameters(Map<String, String> pathParameters) {
		this.pathParameters = pathParameters;
	}

    public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	public String getBusinessName() {
		if (this.headers==null) {
			return StringUtils.EMPTY_STRING;
		}
		return this.headers.get(FunctionBuilder.HEADER_BUSINESS_NAME);
	}
	
	public void setRootOnCollection(Collection<?> childs) {
		RequestRoot requestRoot = this;
		setRootOnCollection(requestRoot, childs);
	}

	public void setRootOnCollection(RequestRoot requestRoot, Collection<?> childs) {
		if (childs!=null) {
			childs.forEach(new Consumer<Object>() {
				@Override
				public void accept(Object requestObject) {
					RequestChild requestChild = (RequestChild) requestObject;
					requestChild.setRequestRoot(requestRoot);
				}
			});
		}
	}
}
