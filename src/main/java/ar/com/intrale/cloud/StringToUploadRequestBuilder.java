package ar.com.intrale.cloud;

import java.util.Map;

import ar.com.intrale.cloud.exceptions.FunctionException;

public class StringToUploadRequestBuilder implements Builder<String, UploadRequest> {

	@Override
	public UploadRequest build(Map<String, String> headers, String source) throws FunctionException {
		UploadRequest request = new UploadRequest();
		request.setHeaders(headers);
		request.setContent(source);
		return request;
	}

}
