package org.cybnity.framework.immutable.data;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.LinkedList;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.MutableProperty;

/**
 * Example of specific property regarding an organization, that can be changed
 * (e.g by organization manager when the company's physical address is moved),
 * and which need to historized in an immutable way the history of changes
 * (version of this information).
 * 
 * Sample usable to evaluate the
 * {@link org.cybnity.framework.immutable.MutableProperty} pattern.
 * 
 * @author olivier
 *
 */
public class PhysicalAddressProperty extends MutableProperty {

    private OffsetDateTime versionedAt;

    /**
     * Example of keys set regarding the multiple attribute defining this complex
     * physical address, and that each change need to be versioned/treated as a
     * single atomic fact.
     */
    public enum PropertyAttributeKey {
	Street, City, Country, State;
    }

    public PhysicalAddressProperty(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue)
	    throws IllegalArgumentException {
	super(propertyOwner, propertyCurrentValue);
	this.versionedAt = OffsetDateTime.now();
    }

    public PhysicalAddressProperty(Entity propertyOwner, HashMap<String, Object> propertyCurrentValue,
	    LinkedList<PhysicalAddressProperty> prior) throws IllegalArgumentException {
	super(propertyOwner, propertyCurrentValue, prior);
    }

    @Override
    public OffsetDateTime occurredAt() {
	return this.versionedAt;
    }

    @Override
    public Object immutable() throws CloneNotSupportedException {
	// TODO Auto-generated method stub
	return null;
    }

    /**
     * Get the current value of this complex property.
     * 
     * @return A set of valued attributes.
     */
    public HashMap<String, Object> currentValue() {
	return this.value;
    }

    /**
     * Get the history chain of versions including changed values about this
     * property.
     * 
     * @return A changes history. Empty list by default.
     */
    public LinkedList<PhysicalAddressProperty> changesHistory() {
	// Read previous changes history (not including the current version)
	LinkedList<PhysicalAddressProperty> history = new LinkedList<>();
	for (MutableProperty previousChangedProperty : this.prior) {
	    history.add((PhysicalAddressProperty) previousChangedProperty);
	}
	return history;
    }

    /**
     * Who is the owner of this property
     * 
     * @return The owner
     * @throws CloneNotSupportedException If impossible creation of immutable
     *                                    version of instance
     */
    public Entity owner() throws CloneNotSupportedException {
	return (Entity) this.entity.immutable();
    }

}
