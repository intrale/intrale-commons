package ar.com.intrale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.com.intrale.in.IntraleFunction;


public abstract class CloudBaseApplicationTest {

	private static final String DESCRIPTION = "description";

	private static final String CODE = "code";

	private static final String UNEXPECTED = "UNEXPECTED";

	@Autowired @Qualifier(IntraleFunction.NAME)
	private IntraleFunction function;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Test
	public void contextLoads() {
		assertThat(function).isNotNull();
	}
	
	@Test
	public void requestNull() {
		ResponseEntity<String> response = function.execute(null, null);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		try {
			Collection<Map> errors = objectMapper.readValue(response.getBody(), Collection.class);
			if (errors==null) {
				fail();
			}
			Iterator<Map> it = errors.iterator();
			Boolean finded = Boolean.FALSE;
			while (it.hasNext()) {
				Map error = (Map) it.next();
				if (UNEXPECTED.equals(error.get(CODE)) && ((String)error.get(DESCRIPTION)).contains("IllegalArgumentException")) {
					finded = Boolean.TRUE;
				}
			}
			assertTrue(finded);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void requestEmpty() {
		ResponseEntity<String> response = function.execute(null, "");
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		try {
			Collection<Map> errors = objectMapper.readValue(response.getBody(), Collection.class);
			if (errors==null) {
				fail();
			}
			Iterator<Map> it = errors.iterator();
			Boolean finded = Boolean.FALSE;
			while (it.hasNext()) {
				Map error = (Map) it.next();
				if (IntraleFunction.BODY_PARSE_EXCEPTION.equals(error.get(CODE)) 
						&& ((String)error.get(DESCRIPTION)).contains(IntraleFunction.THE_BODY_JSON_COULD_NOT_BE_PARSED)) {
					finded = Boolean.TRUE;
				}
			}
			assertTrue(finded);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void requestMalformed() {
		ResponseEntity<String> response = function.execute(null, "asdf1431342  1431234 }}ñññ");
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		try {
			Collection<Map> errors = objectMapper.readValue(response.getBody(), Collection.class);
			if (errors==null) {
				fail();
			}
			Iterator<Map> it = errors.iterator();
			Boolean finded = Boolean.FALSE;
			while (it.hasNext()) {
				Map error = (Map) it.next();
				if (IntraleFunction.BODY_PARSE_EXCEPTION.equals(error.get(CODE)) 
						&& ((String)error.get(DESCRIPTION)).contains(IntraleFunction.THE_BODY_JSON_COULD_NOT_BE_PARSED)) {
					finded = Boolean.TRUE;
				}
			}
			assertTrue(finded);
		} catch (Exception e) {
			fail();
		}
	}
	
}
