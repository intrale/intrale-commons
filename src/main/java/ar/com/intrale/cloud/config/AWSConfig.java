package ar.com.intrale.cloud.config;

public class AWSConfig {
    
	private String clientId;
    private String userPoolId;
    private String region;
    
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getUserPoolId() {
		return userPoolId;
	}
	public void setUserPoolId(String userPoolId) {
		this.userPoolId = userPoolId;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
    
}
