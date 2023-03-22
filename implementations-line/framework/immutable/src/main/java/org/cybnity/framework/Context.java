package org.cybnity.framework;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Utility class providing generic features to manage properties and resources
 * containers that can be specialized by sub-classes regarding specific types of
 * resources or specialized IContext sub-interfaces.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public class Context implements IContext {

    /**
     * Container of resources instances provided via resource item containers.
     */
    private ConcurrentHashMap<String, ResourceItem> resources;

    /**
     * Default constructor.
     */
    public Context() {
	// Initialize generic container of resources
	resources = new ConcurrentHashMap<String, ResourceItem>();
    }

    @Override
    public boolean remove(String resourceName) {
	boolean wasRemoved = false;
	if (resourceName != null && !resourceName.equals("")) {
	    // Find existant resources with equals name
	    wasRemoved = (resources.remove(resourceName) != null);
	}
	return wasRemoved;
    }

    @Override
    public boolean addResource(final Object instance, String resourceName, boolean forceReplace)
	    throws IllegalArgumentException {
	if (instance == null)
	    throw new IllegalArgumentException("instance parameter is required!");
	boolean managed = false;
	// Prepare container of resource in final reference
	final ResourceItem item = new ResourceItem(instance);
	String resourceId = resourceName;
	if (resourceId == null || resourceId.equals("")) {
	    // Define the resource identifier from the object class type
	    resourceId = instance.getClass().getName();
	}
	// Check if a resource is already existing into the bundle with the same
	// identifier before to store in the bundle
	ResourceItem existentPreviousVersion = resources.get(resourceId);
	if (existentPreviousVersion == null) {
	    // Add resource in bundle
	    resources.put(resourceId, item);
	    managed = true;
	} else {
	    // Replace the existent version according to the requested forcing action
	    if (forceReplace) {
		// Update current instance in container (which is perhaps currently in read by
		// other reference, and which shall not generate a NullPointerException if this
		// reference was removed from the bundle)
		existentPreviousVersion.setSourcedInstance(instance);
	    }
	}
	return managed;
    }

    @Override
    public Object get(Class<?> typeOfResult) {
	Object found = null;
	if (typeOfResult != null) {
	    // Search resource definition including a type of object equals to the requested
	    // result type
	    for (Entry<String, ResourceItem> item : resources.entrySet()) {
		Class<?> itemType = item.getValue().getResourceType();
		itemType.getTypeName();
		if (itemType.getName().equals(typeOfResult.getName())) {
		    found = item.getValue().getSourcedInstance();
		    break; // Stop research
		}
	    }
	}
	return found;
    }

    @Override
    public Object get(String resourceName) {
	Object foundRef = null;
	if (resourceName != null && !resourceName.equals("")) {
	    // Search resource definition with equals name
	    ResourceItem item = resources.get(resourceName);
	    if (item != null) {
		// Get the resource object
		foundRef = item.getSourcedInstance();
	    }
	}
	return foundRef;
    }

    /**
     * Represent a container of resource description (e.g additional metadata
     * allowed on the usage stats or attributes helping to categorization or search
     * of instance).
     */
    protected class ResourceItem {

	private Object sourcedInstance;
	private Class<?> resourceType;

	/**
	 * Default constructor.
	 * 
	 * @param sourcedInstance Mandatory instance of the resource that is
	 *                        containerizze and that should be providable as
	 *                        resource.
	 * @throws IllegalArgumentException When mandatory parameter is missing.
	 */
	public ResourceItem(final Object sourcedInstance) throws IllegalArgumentException {
	    this.sourcedInstance = sourcedInstance;
	    resourceType = sourcedInstance.getClass();
	}

	protected Object getSourcedInstance() {
	    return this.sourcedInstance;
	}

	protected void setSourcedInstance(Object sourcedInstance) {
	    this.sourcedInstance = sourcedInstance;
	}

	protected Class<?> getResourceType() {
	    return this.resourceType;
	}

    }

    @Override
    public String get(IReadableConfiguration config) throws IllegalArgumentException {
	if (config == null)
	    throw new IllegalArgumentException("The config parameter is required!");
	// Read the current system environment variable
	return System.getenv(config.getName());
    }

}
