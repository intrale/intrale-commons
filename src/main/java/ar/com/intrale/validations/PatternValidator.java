package ar.com.intrale.validations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import ar.com.intrale.in.Error;

public abstract class PatternValidator extends Validator {

	private Pattern pattern;
	private Matcher matcher;
	
	public PatternValidator(String reference, String value) {
		super(reference, value);
		pattern = Pattern.compile(getPattern());
	}
	
	public abstract String getPattern();

	@Override
	public Error validate() {
		if (!StringUtils.isEmpty(this.value)) {
			matcher = pattern.matcher(this.value);
			if(!matcher.matches()) {
				StringBuilder codeBuilder = new StringBuilder();
				codeBuilder.append("field_").append(this.reference).append(getCodeSufix());
				
				StringBuilder descriptionBuilder = new StringBuilder();
				descriptionBuilder.append("Field ").append(this.reference).append(getDescriptionError());
				
				return new Error(codeBuilder.toString(), descriptionBuilder.toString());
			} 
		}
		return null;
	}

	protected abstract String getDescriptionError();

	protected abstract String getCodeSufix();

}
