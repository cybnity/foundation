package org.cybnity.framework.domain.model.sample.readmodel;

/**
 * Example of denormalized version of Identifier domain object shareable by a
 * ReadModel with external systems.
 * 
 * @author olivier
 *
 */
public class IdentifierDTO {

    private final String name;
    private final String value;

    public IdentifierDTO(String name, String value) {
	this.name = name;
	this.value = value;
    }

    public String getValue() {
	return this.value;
    }

    public String getName() {
	return this.name;
    }

}
