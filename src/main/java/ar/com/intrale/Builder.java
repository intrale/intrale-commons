package ar.com.intrale;

import java.util.Map;

import ar.com.intrale.exceptions.FunctionException;

public interface Builder <SOURCE, TARGET>{
	
	TARGET build(Map <String, String> headers, Map <String, String> queryStringParameters, SOURCE source) throws FunctionException ;

}
