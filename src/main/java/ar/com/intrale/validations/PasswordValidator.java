package ar.com.intrale.validations;

public class PasswordValidator extends PatternValidator {

	public PasswordValidator(String reference, String value) {
		super(reference, value);
	}

	/**
			 ^                 # start-of-string
			(?=.*[0-9])       # a digit must occur at least once
			(?=.*[a-z])       # a lower case letter must occur at least once
			(?=.*[A-Z])       # an upper case letter must occur at least once
			(?=.*[@#$%^&+=])  # a special character must occur at least once
			(?=\S+$)          # no whitespace allowed in the entire string
			.{8,}             # anything, at least eight places though
			$                 # end-of-string
	 */
	
	@Override
	public String getPattern() {
		return "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=?!\"/\\,><':;|_.*[@#$%^&+=])(?=\\S+$).{6,}$";
	}

	@Override
	protected String getDescriptionError() {
		return " does not have a valid format (" + getPattern() + ")";
	}

	@Override
	protected String getCodeSufix() {
		return "_format";
	}

}
