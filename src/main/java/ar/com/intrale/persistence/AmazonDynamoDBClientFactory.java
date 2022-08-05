package ar.com.intrale.persistence;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import ar.com.intrale.IntraleFactory;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;

@Factory
@Requires(property = IntraleFactory.DYNAMODB_CLIENT, value = IntraleFactory.TRUE, defaultValue = IntraleFactory.FALSE)
public class AmazonDynamoDBClientFactory extends 
			IntraleFactory<AmazonDynamoDB>
{

	private static final Logger LOGGER = LoggerFactory.getLogger(AmazonDynamoDBClientFactory.class);

	@Singleton
	public AmazonDynamoDB provider() {
		LOGGER.info("Instanciando AmazonDynamoDB desde AmazonDynamoDBFactory:" + config.getInstantiate().getProvider());
		BasicAWSCredentials credentials = new BasicAWSCredentials(config.getDatabase().getAccess(), config.getDatabase().getSecret());
    	
        AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
          .withCredentials(new AWSStaticCredentialsProvider(credentials))
          .withRegion(config.getAws().getRegion())
          .build();
         
        return amazonDynamoDB;
	}
}
