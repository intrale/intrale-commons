package ar.com.intrale.cloud;

public interface BuilderForLambda <SOURCE, TARGET> extends Builder<SOURCE, TARGET> {

	public Object wrapForLambda(TARGET target);
	
}
