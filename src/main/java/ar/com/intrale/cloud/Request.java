package ar.com.intrale.cloud;

import javax.validation.constraints.NotBlank;

import edu.umd.cs.findbugs.annotations.NonNull;

public class Request {
    @NonNull
    @NotBlank
	private String businessName;
    
    private String requestId;

	@NonNull
    @NotBlank
    public String getRequestId() {
		return requestId;
	}

	public void setRequestId(@NonNull String requestId) {
		this.requestId = requestId;
	}

	@NonNull
    @NotBlank
    public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(@NonNull String businessName) {
		this.businessName = businessName;
	}
}
