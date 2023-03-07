package org.cybnity.framework.immutable;

/**
 * Represent a child of a Transaction (as predecessor) that capture one specific
 * version of a mutable property relative to a parent entity to change.
 * 
 * @author olivier
 *
 */
public class TransactionItem {

    /**
     * The entity which is subject of a property value change.
     */
    private Entity itemContext;

    /**
     * Version of property changed regarding the parent entity context.
     */
    private MutableProperty propertyState;

    public TransactionItem(Entity childEntityContext, MutableProperty childPropertyVersion)
	    throws IllegalArgumentException {
	if (childEntityContext == null)
	    throw new IllegalArgumentException("childEntityContext parameter is required!");
	if (childPropertyVersion == null)
	    throw new IllegalArgumentException("childPropertyVersion parameter is required!");
	this.itemContext = childEntityContext;
	this.propertyState = childPropertyVersion;
    }

    /**
     * Get the owner of the property which is the context of this item.
     * 
     * @return A parent immutable version.
     * @throws ImmutabilityException When problem to instantiate the immutable
     *                               version returned.
     */
    public Entity getItemContext() throws ImmutabilityException {
	return (Entity) this.itemContext.immutable();
    }

    /**
     * Get the property version captured by this item.
     * 
     * @return A property immutable version.
     * @throws ImmutabilityException When problem to instantiate the immutable
     *                               version returned.
     */
    public MutableProperty getPropertyState() throws ImmutabilityException {
	return (MutableProperty) this.propertyState.immutable();
    }
}
