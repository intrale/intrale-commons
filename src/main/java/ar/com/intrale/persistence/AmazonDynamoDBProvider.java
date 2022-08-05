package ar.com.intrale.persistence;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;

public abstract class AmazonDynamoDBProvider<ENTITY extends Entity> implements PersitenceProvider<ENTITY>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AmazonDynamoDBProvider.class);
	
	protected final Class<ENTITY> providerType = (Class<ENTITY>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	
	@Inject
	protected AmazonDynamoDB amazonDynamoDB;
	
	@Inject
	protected DynamoDBMapper dynamoDBMapper;
	
	@Override
	public void save(ENTITY toPersist) {
		dynamoDBMapper.save(toPersist);
	}

	@Override
	public Collection<ENTITY> list(PersistenceFilters filters) {
		DynamoDBScanExpression dbScanExpression = new DynamoDBScanExpression();
		
		StringBuffer filterExpression = new StringBuffer();
		filters.keySet().stream().forEach(new Consumer<String>() {
			@Override
			public void accept(String name) {
				if (filterExpression.length()>0) {
					filterExpression.append(" AND ");
				}
				filterExpression.append(name.replace(":", ""));
				filterExpression.append(" = ");
				filterExpression.append(name);
				filterExpression.append(" ");
			}
		});
		dbScanExpression.withFilterExpression(filterExpression.toString()).withExpressionAttributeValues(filters);
		LOGGER.info("list with => filterExpression:" + filterExpression.toString() + ", filters:" + filters);
		return dynamoDBMapper.scan(providerType, dbScanExpression);
	}

	@Override
	public ENTITY get(PersistenceFilters filters) {
		Collection<ENTITY> list = list(filters);
		if (list.isEmpty()) return null;
		return list.iterator().next();
	}

	@Override
	public void delete(PersistenceFilters filters){
		Map<String, AttributeValue> keys = new HashMap<>();
		filters.forEach(new BiConsumer<String, AttributeValue>() {
			@Override
			public void accept(String name, AttributeValue value) {
				keys.put(name.replace(":", ""), value);
			}
		});
		
		DeleteItemRequest deleteItemRequest = new DeleteItemRequest();
		deleteItemRequest.setTableName(getTableName());

		deleteItemRequest.setKey(keys);
		
		amazonDynamoDB.deleteItem(deleteItemRequest);
	}

	public abstract String getTableName();
	
}
