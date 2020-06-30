package ar.com.intrale.validations;

public class EmailValidator extends PatternValidator {

	public EmailValidator(String reference, ValueValidator valueValidator) {
		super(reference, valueValidator);
	}

	@Override
	public String getPattern() {
		return "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	}

	@Override
	protected String getFinalErrorDescription() {
		return "does not have email format (" + getPattern() + ")";
	}

	@Override
	protected String getPostFix() {
		return "format";
	}

}
