package org.cybnity.framework.immutable.sample;

import java.io.Serializable;
import java.time.OffsetDateTime;

import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.Member;

/**
 * Example of member type regarding an organization.
 * 
 * @author olivier
 *
 */
public class Employee implements Member {

    private static final long serialVersionUID = 1L;
    private String name;
    private Identifier id;
    private OffsetDateTime at;

    public Employee(String name, Identifier id) {
	this.name = name;
	this.id = id;
	this.at = OffsetDateTime.now();
    }

    @Override
    public Identifier identified() {
	return new IdentifierImpl(new String(id.name()), new String(id.value().toString()));
    }

    @Override
    public OffsetDateTime occurredAt() {
	return this.at;
    }

    @Override
    public Serializable immutable() throws CloneNotSupportedException {
	return new Employee(this.name, this.id).at = this.at;
    }

}