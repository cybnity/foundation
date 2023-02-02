package org.cybnity.feature.framework;

import java.util.Collection;

/**
 * Type of resource supported by a CYBNITY solution (e.g Feature, Application,
 * System...)
 */
public interface ResourceType {

	/**
	 * Get the logical name regarding the nature of the resource.
	 *
	 * @return A label.
	 */
	public String name();

	/**
	 * Set of control capabilities allowing to make interactions (e.g read of
	 * information, change of resource values) with this resource type.
	 *
	 * @return A list of controllers allowing to manipulate this resource. Null by
	 *         default.
	 */
	public Collection<ControlledInterface> controllers();
}
