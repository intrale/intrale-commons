package ar.com.intrale.cloud;

import java.util.Map;

import ar.com.intrale.cloud.exceptions.FunctionException;

public interface Builder <SOURCE, TARGET>{
	
	TARGET build(Map <String, String> headers, SOURCE source) throws FunctionException ;

}
