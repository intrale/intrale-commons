package ar.com.intrale.validations;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.BeansException;
import org.springframework.util.StringUtils;

import ar.com.intrale.in.IntraleFunctionException;

public class NotEmptyCollectionValidator extends Validator {

	private Collection<String> values;
	
	private NotEmptyCollectionValidator(String reference, ValueValidator valueValidator) {
		//Dont use this constructor
		super(reference, valueValidator);
	}
	
	public NotEmptyCollectionValidator(String reference, Collection<String> values) {
		super(reference, null);
		this.values = values;
	}	

	@Override
	public void validate() throws BeansException, IntraleFunctionException  {
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
