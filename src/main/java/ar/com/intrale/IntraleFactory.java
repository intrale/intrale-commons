package ar.com.intrale;

import javax.inject.Inject;

import ar.com.intrale.config.ApplicationConfig;

public abstract class IntraleFactory <PROV>{
	
	public static final String TRUE = "true";
	
	public static final String FACTORY = "app.instantiate.factory";
	public static final String PROVIDER = "app.instantiate.provider";
	
	@Inject
	protected ApplicationConfig config;
	
    
    public abstract PROV provider();
	
}
