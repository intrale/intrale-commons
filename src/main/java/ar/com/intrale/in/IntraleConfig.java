package ar.com.intrale.in;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class IntraleConfig {

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
	
}
