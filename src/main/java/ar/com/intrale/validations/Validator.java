package ar.com.intrale.validations;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;

import ar.com.intrale.in.Error;
import ar.com.intrale.in.IntraleFunctionException;

public abstract class Validator {
	
	private static final String WHITE_SPACE = " ";
	private static final String UNDERSCORE = "_";
	private static final String FIELD = "field";
	protected String reference;
	protected Object data;
	
	protected BeanWrapper beanWrapper;

	@Autowired
	protected ApplicationContext applicationContext;
	
	public Validator(String reference, Object data) {
		this.reference = reference;
		this.data = data;
		beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(data);
	}
	
	public abstract void validate()  throws BeansException, IntraleFunctionException;
	
	public Object getValue(String name) {
		return beanWrapper.getPropertyValue(name);
	}
	
	public Object getReferenceValue() {
		return getValue(reference);
	}
	
	public String getErrorCode() {
		StringBuilder errorCode = new StringBuilder();
		errorCode.append(FIELD).append(UNDERSCORE).append(this.reference).append(UNDERSCORE).append(getPostFix());
		return errorCode.toString();
	}
	
	public String getDescriptionError() {
		StringBuilder descriptionBuilder = new StringBuilder();
		descriptionBuilder.append(FIELD).append(WHITE_SPACE).append(this.reference).append(WHITE_SPACE).append(getFinalErrorDescription());
		return descriptionBuilder.toString();
	}

	protected abstract String getFinalErrorDescription();

	protected abstract String getPostFix();
	
	public IntraleFunctionException throwException() throws BeansException, IntraleFunctionException {
		throw (IntraleFunctionException) applicationContext.getBean(IntraleFunctionException.NAME, HttpStatus.BAD_REQUEST, new Error(getErrorCode(), getDescriptionError()));
	}

	
}
