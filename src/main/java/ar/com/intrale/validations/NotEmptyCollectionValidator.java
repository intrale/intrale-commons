package ar.com.intrale.validations;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.util.StringUtils;

import ar.com.intrale.in.Error;

public class NotEmptyCollectionValidator extends Validator {

	private Collection<String> values;
	
	private NotEmptyCollectionValidator(String reference, String value) {
		//Dont use this constructor
		super(reference, value);
	}
	
	public NotEmptyCollectionValidator(String reference, Collection<String> values) {
		super(reference, null);
		this.values = values;
	}	

	@Override
	public Error validate() {
		if (values == null || values.size() <= 0 ) {
			StringBuilder codeBuilder = new StringBuilder();
			codeBuilder.append("field_").append(this.reference).append("_empty");
			
			StringBuilder descriptionBuilder = new StringBuilder();
			descriptionBuilder.append("Field ").append(this.reference).append(" cannot be empty.");
			
			return new Error(codeBuilder.toString(), descriptionBuilder.toString());
		} else {
			Iterator<String> it = values.iterator();
			while (it.hasNext()) {
				String actual = (String) it.next();
				if (StringUtils.isEmpty(actual)) {
					StringBuilder codeBuilder = new StringBuilder();
					codeBuilder.append("field_").append(this.reference).append("_emptyContent");
					
					StringBuilder descriptionBuilder = new StringBuilder();
					descriptionBuilder.append("Field ").append(this.reference).append(" cannot contain empty values.");
					
					return new Error(codeBuilder.toString(), descriptionBuilder.toString());
				}
			}
		}
		return null;
	}

}
