package org.cybnity.framework;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Generic context allowing to share and provide informations in an area of
 * usage (e.g into a memore space, into a responsibility moment).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface IContext {

    /**
     * Get an instance of a resource type.
     * 
     * @param typeOfResult Type of resource instance to return. For example, when
     *                     value is an interface type, the context search an object
     *                     which implement this interface contract.
     * @return The found resource instance or null.
     */
    public Object get(Class<?> typeOfResult);

    /**
     * Get an instance of a resource that is identified by a logical name.
     * 
     * @param resourceName The name (e.g identifier, text chain) of the resource to
     *                     search (e.g class type name, property.name).
     * @return The found resource instance or null.
     */
    public Object get(String resourceName);

    /**
     * Add a resource instance into this context.
     * 
     * @param instance     Mandatory instance to record into this context.
     * @param resourceName A logical name of the resource allowing to retrieve it
     *                     from this context. If null or empty label, the method try
     *                     to add the instance with a name equals to the instance
     *                     Class name.
     * @param forceReplace True to replace existant resource that could be already
     *                     found into this context with the same name. False else.
     * @return True when resource was stored in the context. False when storage is
     *         not possible (e.g another resource is already managed by this context
     *         and the new resource can't replace it via this method (e.g
     *         forceReplace parameter is defined as False).
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public boolean addResource(final Object instance, String resourceName, boolean forceReplace)
	    throws IllegalArgumentException;

    /**
     * Remove resource from this context.
     * 
     * @param resourceName Name of the resource instance to remove. Ignored if null
     *                     or empty.
     * @return True when a resource previously managed by this context had been
     *         found and was removed. Else return False.
     */
    public boolean remove(String resourceName);

}
