package ar.com.intrale.config;

public class S3Config extends CredentialsConfig {

	private String bucketName;

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	
}
