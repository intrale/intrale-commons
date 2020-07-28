package ar.com.intrale.cloud;

import javax.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

@Factory
public class BeanFactory {

	@Bean @Singleton
	public ObjectMapper ObjectMapper() {
		return new ObjectMapper();
	}
	
	
}
