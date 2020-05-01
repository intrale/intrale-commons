package ar.com.intrale.in;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JSONUtils {

	private static final String EMPTY = "";
	
	ObjectMapper mapper = new ObjectMapper();
	
	public Object toObject(String text, Class type) {
		if ((!StringUtils.isEmpty(text)) && (type!=null)) {
			try {
				return mapper.readValue(text, type);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public String toString(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return EMPTY;
	}
	
}
