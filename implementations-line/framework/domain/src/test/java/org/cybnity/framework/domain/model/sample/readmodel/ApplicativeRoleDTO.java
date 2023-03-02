package org.cybnity.framework.domain.model.sample.readmodel;

/**
 * Denormalized version of an applicative role.
 * 
 * @author olivier
 *
 */
public class ApplicativeRoleDTO {

    private String name;

    public ApplicativeRoleDTO(String name) {
	this.name = name;
    }

    public String getName() {
	return this.name;
    }

}
