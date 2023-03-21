package org.cybnity.framework.domain.model.sample;

import org.cybnity.framework.domain.ValueObject;

/**
 * Example of value object defining a name of company.
 * 
 * @author olivier
 *
 */
public class CompanyNameObject extends ValueObject<String> {

    private String name;

    public CompanyNameObject(String name) {
	this.name = name;
    }

    @Override
    public String[] valueHashCodeContributors() {
	return new String[] { this.name };
    }

}
