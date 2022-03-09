package ar.com.intrale.config;

public class CognitoConfig extends CredentialsConfig {

	private String clientId;
    private String userPoolId;
    private Integer connectionTimeout;
    private Integer readTimeout;
    private String urlPrefix;
    private String urlMid;
    private String urlSufix;
    
	public String getUrlPrefix() {
		return urlPrefix;
	}
	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}
	public String getUrlMid() {
		return urlMid;
	}
	public void setUrlMid(String urlMid) {
		this.urlMid = urlMid;
	}
	public String getUrlSufix() {
		return urlSufix;
	}
	public void setUrlSufix(String urlSufix) {
		this.urlSufix = urlSufix;
	}
	public Integer getConnectionTimeout() {
		return connectionTimeout;
	}
	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public Integer getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}
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
	
}
