package ar.com.intrale;

import javax.inject.Inject;

import ar.com.intrale.config.ApplicationConfig;

/**
 * 
 * Importante mantener la relacion 1 provedor 1 factory
 * para orden y claridad del codigo
 * En caso de necesitar crear mas de un proveedor
 * cree una nueva factory
 *
 * @param <PROV>
 */
public abstract class IntraleFactory <PROV>{
	
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	
	public static final String APP =   "app";
	public static final String INSTANTIATE = APP + ".instantiate";
	public static final String INSTANTIATE_AWS = INSTANTIATE + ".aws";
	public static final String AWS_DYNAMODB = INSTANTIATE_AWS + ".dynamodb";
	public static final String DYNAMODB_CLIENT = AWS_DYNAMODB + ".client";
	public static final String DYNAMODB_MAPPER = AWS_DYNAMODB + ".mapper";
	
	public static final String INSTANTIATE_FIREBASE = INSTANTIATE + ".firebase";
	public static final String FIREBASE_MESSAGING = INSTANTIATE_FIREBASE + ".messaging";
	
	public static final String FACTORY =   "app.instantiate.factory";
	public static final String PROVIDER =  "app.instantiate.provider";
	public static final String PERSISTER = "app.instantiate.persister";
	public static final String MAPPER = "app.instantiate.mapper";
	
	@Inject
	protected ApplicationConfig config;
	
    
    public abstract PROV provider();
	
}
