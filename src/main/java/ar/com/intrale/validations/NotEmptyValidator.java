package ar.com.intrale.validations;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import ar.com.intrale.in.IntraleFunctionException;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NotEmptyValidator extends Validator {

	public NotEmptyValidator(String reference, Object data) {
		super(reference, data);
	}

	@Override
	public void validate() throws BeansException, IntraleFunctionException {
		if (StringUtils.isEmpty(getReferenceValue())) {
			throwException();
		}
	}

	@Override
	protected String getFinalErrorDescription() {
		return "empty";
	}

	@Override
	protected String getPostFix() {
		return "cannot be empty.";
	}

}
