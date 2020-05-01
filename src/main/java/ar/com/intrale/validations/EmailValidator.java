package ar.com.intrale.validations;

public class EmailValidator extends PatternValidator {

	public EmailValidator(String reference, String value) {
		super(reference, value);
	}

	@Override
	public String getPattern() {
		return "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	}

	@Override
	protected String getDescriptionError() {
		return " does not have email format (" + getPattern() + ")";
	}

	@Override
	protected String getCodeSufix() {
		return "_format";
	}

}
