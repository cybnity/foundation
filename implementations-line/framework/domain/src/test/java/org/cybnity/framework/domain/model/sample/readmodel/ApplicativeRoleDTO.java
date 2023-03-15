package org.cybnity.framework.domain.model.sample.readmodel;

import org.cybnity.framework.immutable.HistoryState;

/**
 * Denormalized version of an applicative role.
 * 
 * @author olivier
 *
 */
public class ApplicativeRoleDTO {

    private String name;
    
    /**
     * Default defined as Committed.
     */
    public HistoryState status = HistoryState.COMMITTED;

    public ApplicativeRoleDTO(String name) {
	this.name = name;
    }

    public String getName() {
	return this.name;
    }

}
