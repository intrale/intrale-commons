package ar.com.intrale.cloud;

import java.util.function.Function;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

@Controller("/")
public class MicroService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MicroService.class);
	
	@Inject
	private Function function;
	
	@Post()
	public HttpResponse<String> post (String request) {
		return (HttpResponse<String>) function.apply(request);
	}
	
}
