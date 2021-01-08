package ar.com.intrale.cloud;

import javax.inject.Singleton;

import io.micronaut.context.annotation.Value;

@Singleton
public class Config {
	
	private static final String COGNITO_URL_PREFIX = "https://cognito-idp.";
	private static final String COGNITO_URL_MID = ".amazonaws.com/";
	private static final String COGNITO_URL_SUFIX = "/.well-known/jwks.json";

	@Value("${aws.userPoolId}")
	private String userPoolId;
	
	@Value("${aws.region}")
	private String region;
	
	@Value("${aws.clientId}" )
	private String clientId;
	
	//@Value("${aws.connectionTimeout:2000}")
	//private Integer connectionTimeout;
	
	//@Value("${aws.readTimeout:2000}")
	//private Integer readTimeout;
	
	@Value("${aws.cognito.accessKey}")
	private String cognitoAccessKey;
	
	@Value("${aws.cognito.secretKey:}")
	private String cognitoSecretKey;
	
	public String getCognitoAccessKey() {
		return cognitoAccessKey;
	}

	public void setCognitoAccessKey(String cognitoAccessKey) {
		this.cognitoAccessKey = cognitoAccessKey;
	}

	public String getCognitoSecretKey() {
		return cognitoSecretKey;
	}

	public void setCognitoSecretKey(String cognitoSecretKey) {
		this.cognitoSecretKey = cognitoSecretKey;
	}

	/*public String getUserPoolIdUrl() {
		StringBuilder jwtUrl = new StringBuilder();
		jwtUrl.append(COGNITO_URL_PREFIX).append(region).append(COGNITO_URL_MID).append(userPoolId);
		return jwtUrl.toString();
	}*/
	
	/*@Bean
	public ConfigurableJWTProcessor JWTProcessor() throws MalformedURLException {

		ResourceRetriever resourceRetriever = new DefaultResourceRetriever(connectionTimeout, readTimeout);
		
		StringBuilder jwtUrl = new StringBuilder();
		jwtUrl.append(getUserPoolIdUrl()).append(COGNITO_URL_SUFIX);
	    URL jwkSetURL= new URL(jwtUrl.toString());
	    
	    //Creates the JSON Web Key (JWK)
	    JWKSource keySource= new RemoteJWKSet(jwkSetURL, resourceRetriever);
	  
	    //RSASSA-PKCS-v1_5 using SHA-256 hash algorithm
	    JWSKeySelector keySelector= new JWSVerificationKeySelector(JWSAlgorithm.RS256, keySource);
	    
	    ConfigurableJWTProcessor processor = new DefaultJWTProcessor();
	    processor.setJWSKeySelector(keySelector);
	    return processor;
	}*/

	/*public Integer getConnectionTimeout() {
		return connectionTimeout;
	}
	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}*/
	/*public Integer getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}*/
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

