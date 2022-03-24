package ar.com.intrale;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import ar.com.intrale.exceptions.FunctionException;
import ar.com.intrale.exceptions.UnexpectedException;
import io.micronaut.core.util.StringUtils;

public abstract class ReadBaseFunction<
						FUNCTION_REQ extends RequestRoot, 
						FUNCTION_RES extends ReadResponse, 
						PROV extends AmazonDynamoDB, 
						REQ_BUILDER extends Builder, 
						RES_BUILDER extends BuilderForLambda,
						ENTITY>
		extends BaseFunction<FUNCTION_REQ, FUNCTION_RES, PROV, REQ_BUILDER, RES_BUILDER> {
			
			protected final Class<ReadResponse> responseType = (Class<ReadResponse>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
			protected final Class<?> entityType = (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[5];

			public static final String TWO_POINTS = ":";
			public static final String ATT = "ATT";
			public static final String EQUAL = ATT + " = " + TWO_POINTS + ATT;
			
			public FUNCTION_RES execute(FUNCTION_REQ request)	throws FunctionException {
				FUNCTION_RES response = instanceFunctionResponse(); 
				
				DynamoDBMapper mapper = new DynamoDBMapper(provider);
				DynamoDBScanExpression dbScanExpression = new DynamoDBScanExpression();
				
				// dynamoDB filters
				addEqualFilter(dbScanExpression, FunctionConst.BUSINESS_NAME, request.getBusinessName());
				
				PaginatedList<ENTITY> list = (PaginatedList<ENTITY>) mapper.scan(entityType, dbScanExpression);
				
				if (!list.isEmpty()) {
					list.forEach(new Consumer<ENTITY>() {
						@Override
						public void accept(ENTITY deliveryLocation) {
							// filter list with request criteria
							Boolean needsToBeFiltered = isNeedsToBeFiltered(request, deliveryLocation);
							
							if (!needsToBeFiltered) {
								response.add(modelToRequest(deliveryLocation));
							}
						}

					});
				}
				
		       return response;
				
			}

			private FUNCTION_RES instanceFunctionResponse() throws UnexpectedException {
				FUNCTION_RES response = null;
				try {
					response = (FUNCTION_RES) responseType.getDeclaredConstructor().newInstance();
				} catch (Exception e) {
					throw new UnexpectedException(new Error("RESPONSE_TYPE_ERROR", FunctionException.toString(e)), mapper);
				}
				return response;
			}
			
			protected abstract Object modelToRequest(ENTITY entity);

			private void addEqualFilter(DynamoDBScanExpression dbScanExpression, String property, String value) {
				if (StringUtils.isNotEmpty(value)) {
					Map<String, AttributeValue> values = new HashMap<String, AttributeValue>();
					values.put(TWO_POINTS + property, new AttributeValue().withS(value));
					dbScanExpression.withFilterExpression(EQUAL.replaceAll(ATT, property)).withExpressionAttributeValues(values);
				}
			}

			/**
			 * Retorna verdadero si necesita ser filtrado y NO retornado junto con el response
			 * @param request
			 * @param deliveryLocation
			 * @return
			 */
			protected abstract Boolean isNeedsToBeFiltered(FUNCTION_REQ request, ENTITY entity);
			
			
}
