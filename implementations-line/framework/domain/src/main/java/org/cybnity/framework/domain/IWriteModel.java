package org.cybnity.framework.domain;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Also named Command Model, it's a segregation element of the CQRS pattern.
 * <br>
 * Event store that publishes events (e.g for refresh by read models) after they
 * have been saved. It's a component of the write side of an application.
 * <br>
 * Typically, receive command events (as entry or interaction point) from other
 * systems (e.g application user interface) through declared API (e.g specific
 * services exposed by the model) or generic entry point (e.g receiving any
 * commands).
 * <br>
 * The implementation system of this role is optimized for writes by being fully
 * normalized.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface IWriteModel {

    /**
     * Process the realization of a command.
     *
     * @param command Mandatory command that must be treated by the model.
     * @throws IllegalArgumentException When command parameter is null or when a
     *                                  command processing can't be performed for
     *                                  cause of command invalidity (e.g missing
     *                                  required contents).
     */
    public void handle(Command command) throws IllegalArgumentException;
}
