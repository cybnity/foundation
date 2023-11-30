package org.cybnity.framework.immutable;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.io.Serializable;

/**
 * Contract regarding an immutable topic.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public interface Unmodifiable {

    /**
     * Get an immutable copy of the object.
     * 
     * @return An unmodifiable version of this instance.
     * @throws ImmutabilityException When impossible creation of immutable version.
     */
    public Serializable immutable() throws ImmutabilityException;
}
