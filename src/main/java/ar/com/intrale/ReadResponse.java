package ar.com.intrale;

import java.util.ArrayList;
import java.util.Collection;

public abstract class ReadResponse<FINDED_ENTITY> extends Response {
	
	private Collection<FINDED_ENTITY> findings = new ArrayList<FINDED_ENTITY>();

	public Collection<FINDED_ENTITY> getFindings() {
		return findings;
	}

	public void setFindings(Collection<FINDED_ENTITY> findings) {
		this.findings = findings;
	}

	public void add(FINDED_ENTITY findedEntity) {
		findings.add(findedEntity);
	}
	
}
