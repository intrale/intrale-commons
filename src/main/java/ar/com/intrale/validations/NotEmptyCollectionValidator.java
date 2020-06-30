package ar.com.intrale.validations;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.BeansException;
import org.springframework.util.StringUtils;

import ar.com.intrale.in.IntraleFunctionException;

public class NotEmptyCollectionValidator extends Validator {

	public NotEmptyCollectionValidator(String reference, ValueValidator valueValidator) {
		super(reference, valueValidator);
	}

	@Override
	public void validate() throws BeansException, IntraleFunctionException  {
		Collection values = (Collection) valueValidator.getValue();
		if (values == null || values.size() <= 0 ) {
			throwException();
		} else {
			Iterator<String> it = values.iterator();
			while (it.hasNext()) {
				String actual = (String) it.next();
				if (StringUtils.isEmpty(actual)) {
					throwException();
				}
			}
		}
	}

	@Override
	protected String getFinalErrorDescription() {
		return "cannot be empty or cannot contain empty values";
	}

	@Override
	protected String getPostFix() {
		return "empty";
	}

}
