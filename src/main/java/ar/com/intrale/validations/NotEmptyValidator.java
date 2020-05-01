package ar.com.intrale.validations;

import org.springframework.util.StringUtils;

import ar.com.intrale.in.Error;

public class NotEmptyValidator extends Validator {

	public NotEmptyValidator(String reference, String value) {
		super(reference, value);
	}

	@Override
	public Error validate() {
		if (StringUtils.isEmpty(this.value)) {
			StringBuilder codeBuilder = new StringBuilder();
			codeBuilder.append("field_").append(this.reference).append("_empty");
			
			StringBuilder descriptionBuilder = new StringBuilder();
			descriptionBuilder.append("Field ").append(this.reference).append(" cannot be empty.");
			
			return new Error(codeBuilder.toString(), descriptionBuilder.toString());
		}
		return null;
	}

}
