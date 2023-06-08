package org.cybnity.framework.domain.model.sample;

import java.io.Serializable;

import org.cybnity.framework.domain.ValueObject;
import org.cybnity.framework.immutable.ImmutabilityException;

/**
 * Example of value object defining a name of company.
 * 
 * @author olivier
 *
 */
public class CompanyNameObject extends ValueObject<String> implements Serializable {

	private String name;

	public CompanyNameObject(String name) {
		this.name = name;
	}

	@Override
	public String[] valueHashCodeContributors() {
		return new String[] { this.name };
	}

	@Override
	public Serializable immutable() throws ImmutabilityException {
		return new CompanyNameObject(this.name);
	}

}
