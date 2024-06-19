package org.cybnity.framework.domain.model;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Specification which is going to capture all the relevant domains knowledge from the
 * domain experts, to determine the scope that is expressed in source code
 * constantly maintained to reflect evolutionary changes in the domain.
 * <p>
 * A referential model for a domain, a specification (e.g defined via
 * sub-interface of this one) provide several types of definitions regarding
 * domain's entities, value objects, services and ubiquitous language elements
 * usable in the domain.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface IDomainModel {

    /**
     * Name of the domain that is owner of this model.
     *
     * @return A name.
     */
    String domainName();
}
