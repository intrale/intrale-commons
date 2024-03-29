package ar.com.intrale.config;

import io.micronaut.context.annotation.ConfigurationBuilder;
import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("app")
public class ApplicationConfig {
	
	private String name;
	private Boolean microservices;

	@ConfigurationBuilder("instantiate")
	protected InstantiateConfig instantiate = new InstantiateConfig();

	@ConfigurationBuilder("activity")
	protected ActivityConfig activity = new ActivityConfig();

	@ConfigurationBuilder("aws")
	protected AWSConfig aws = new AWSConfig();
	
	@ConfigurationBuilder("database")
	protected CredentialsConfig database = new CredentialsConfig();
	
	@ConfigurationBuilder("s3")
	protected S3Config s3 = new S3Config();
	
	@ConfigurationBuilder("cognito")
	protected CognitoConfig cognito = new CognitoConfig();
	

	public S3Config getS3() {
		return s3;
	}

	public void setS3(S3Config s3) {
		this.s3 = s3;
	}

	public CognitoConfig getCognito() {
		return cognito;
	}

	public void setCognito(CognitoConfig cognito) {
		this.cognito = cognito;
	}

	public CredentialsConfig getDatabase() {
		return database;
	}

	public void setDatabase(CredentialsConfig database) {
		this.database = database;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getMicroservices() {
		return microservices;
	}

	public void setMicroservices(Boolean microservices) {
		this.microservices = microservices;
	}

	public InstantiateConfig getInstantiate() {
		return instantiate;
	}

	public void setInstantiate(InstantiateConfig instantiate) {
		this.instantiate = instantiate;
	}

	public ActivityConfig getActivity() {
		return activity;
	}

	public void setActivity(ActivityConfig activity) {
		this.activity = activity;
	}

	public AWSConfig getAws() {
		return aws;
	}

	public void setAws(AWSConfig aws) {
		this.aws = aws;
	}
	

	
}
