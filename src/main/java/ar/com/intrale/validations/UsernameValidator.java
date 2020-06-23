package ar.com.intrale.validations;

public class UsernameValidator extends PatternValidator {

	public UsernameValidator(String reference, String value) {
		super(reference, value);
	}

	@Override
	public String getPattern() {
		return "^[A-Za-z0-9_-]{8,15}$";
	}

	@Override
	protected String getFinalErrorDescription() {
		return "does not have a valid format (" + getPattern() + ")";
	}

	@Override
	protected String getPostFix() {
		return "format";
	}

}
