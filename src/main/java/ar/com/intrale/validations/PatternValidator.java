package ar.com.intrale.validations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.BeansException;
import org.springframework.util.StringUtils;

import ar.com.intrale.in.IntraleFunctionException;

public abstract class PatternValidator extends Validator {

	private Pattern pattern;
	private Matcher matcher;
	
	public PatternValidator(String reference, ValueValidator valueValidator) {
		super(reference, valueValidator);
		pattern = Pattern.compile(getPattern());
	}
	
	public abstract String getPattern();

	@Override
	public void validate() throws BeansException, IntraleFunctionException {
		if (!StringUtils.isEmpty(valueValidator.getValue())) {
			matcher = pattern.matcher((String) valueValidator.getValue());
			if(!matcher.matches()) {
				throwException();
			} 
		}
	}

}
