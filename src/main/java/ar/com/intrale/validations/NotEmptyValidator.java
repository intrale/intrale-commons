package ar.com.intrale.validations;

import org.springframework.beans.BeansException;
import org.springframework.util.StringUtils;

import ar.com.intrale.in.IntraleFunctionException;

public class NotEmptyValidator extends Validator {

	public NotEmptyValidator(String reference, ValueValidator valueValidator) {
		super(reference, valueValidator);
	}

	@Override
	public void validate() throws BeansException, IntraleFunctionException {
		if (StringUtils.isEmpty(valueValidator.getValue())) {
			throwException();
		}
	}

	@Override
	protected String getFinalErrorDescription() {
		return "cannot_be_empty.";
	}

	@Override
	protected String getPostFix() {
		return "empty";
	}

}
