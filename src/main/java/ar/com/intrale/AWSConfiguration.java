package ar.com.intrale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:AwsCredentials.properties")
public class AWSConfiguration {
	
	@Value("${userPoolId}")
	private String userPoolId;
	
	@Value("${region}")
	private String region;
	
	@Value("${clientId}")
	private String clientId;

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

