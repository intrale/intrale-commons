package ar.com.intrale.cloud;

import java.util.Map;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.core.util.StringUtils;

@JsonIgnoreProperties(value = { "headers", "queryStringParameters" })
public class Request {
	
	private Map <String, String> headers;
	
	private Map <String, String> pathParameters;

	@NonNull
    @NotBlank
    private String requestId;


	public Map<String, String> getPathParameters() {
		return pathParameters;
	}

	public void setPathParameters(Map<String, String> pathParameters) {
		this.pathParameters = pathParameters;
	}

	@NonNull
    @NotBlank
    public String getRequestId() {
		return requestId;
	}

	public void setRequestId(@NonNull String requestId) {
		this.requestId = requestId;
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
	
}
