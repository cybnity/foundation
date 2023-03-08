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

    @Override
    protected boolean valueEquality(ValueObject<String> obj) {
	boolean isEquals = false;
	if (obj != null && obj instanceof CompanyNameObject) {
	    isEquals = ((CompanyNameObject) obj).name.equals(this.name);
	}
	return isEquals;
    }

}
