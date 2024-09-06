package org.cybnity.framework;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Generic context allowing to share and provide informations in an area of
 * usage (e.g into a memore space, into a responsibility moment).
 *
 * @author olivier
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
    Object get(Class<?> typeOfResult);

    /**
     * Get an instance of a resource that is identified by a logical name.
     * Find the resource name from this container's attributes set firstly.
     * If not found, try to search resource name from current system environment variables.
     *
     * @param resourceName The name (e.g identifier, text chain) of the resource to
     *                     search (e.g class type name, property.name).
     * @return The found resource instance or null.
     */
    Object get(String resourceName);

    /**
     * Read a configuration variable from this context. During the call of a
     * configuration variable, the context search the configuration name from the
     * current system environment variables (e.g valued, existent into the operating
     * system...).
     *
     * @param config Mandatory type of variable supported by this context.
     * @return Value of the environment variable that is current available and
     * valued into the runtime context (e.g value found) as reusable by the
     * context. Null when the configuration variable to search into this
     * context is not available and defined (e.g missing definition into the
     * operating system) on the current runtime environment.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     * @throws SecurityException        If a security manager exists and its
     *                                  {@link SecurityManager#checkPermission
     *                                  checkPermission} method doesn't allow access
     *                                  to the environment variable.
     */
    String get(IReadableConfiguration config) throws IllegalArgumentException, SecurityException;

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
     * not possible (e.g another resource is already managed by this context
     * and the new resource can't replace it via this method). For example,
     * forceReplace parameter is defined as False.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    boolean addResource(final Object instance, String resourceName, boolean forceReplace)
            throws IllegalArgumentException;

    /**
     * Remove resource from this context.
     *
     * @param resourceName Name of the resource instance to remove. Ignored if null
     *                     or empty.
     * @return True when a resource previously managed by this context had been
     * found and was removed. Else return False.
     */
    boolean remove(String resourceName);

}
