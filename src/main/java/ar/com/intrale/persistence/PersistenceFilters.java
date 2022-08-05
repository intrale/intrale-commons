package ar.com.intrale.persistence;

import java.util.HashMap;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class PersistenceFilters extends HashMap<String, AttributeValue> {
	
	public PersistenceFilters() {}
	
	public PersistenceFilters(String name, String value) {
		addFilter(name, value);
	}
	
	public PersistenceFilters addFilter(String name, String value) {
		put(":" + name, new AttributeValue().withS(value));
		return this;
	}
	
	
}
