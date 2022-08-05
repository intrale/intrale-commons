package ar.com.intrale.persistence;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import ar.com.intrale.IntraleFactory;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;

@Factory
@Requires(property = IntraleFactory.DYNAMODB_MAPPER, value = IntraleFactory.TRUE, defaultValue = IntraleFactory.FALSE)
public class AmazonDynamoDBMapperFactory extends IntraleFactory<DynamoDBMapper> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AmazonDynamoDBMapperFactory.class);
	
   	@Inject
   	protected ApplicationContext applicationContext;

	@Singleton @Requires(beans = {AmazonDynamoDB.class})
	@Override
	public DynamoDBMapper provider() {
		LOGGER.info("Instanciando DynamoDBMapper desde AmazonDynamoDBFactory:" + config.getInstantiate().getProvider());
        return new DynamoDBMapper(applicationContext.getBean(AmazonDynamoDB.class));
	}

}
