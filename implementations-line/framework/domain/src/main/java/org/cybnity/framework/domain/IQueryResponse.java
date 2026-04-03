package org.cybnity.framework.domain;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.util.Optional;

/**
 * Represent a state of data-view provided as result of a query executed with or without filtering parameter.
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface IQueryResponse {

    /**
     * Get a data-view status including specific values.
     *
     * @return A container of one or several data-view status.
     */
    public Optional<DataTransferObject> value();
}
