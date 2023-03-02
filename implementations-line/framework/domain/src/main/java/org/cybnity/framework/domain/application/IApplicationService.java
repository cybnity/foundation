package org.cybnity.framework.domain.application;

import org.cybnity.framework.domain.IWriteModel;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Applicative behaviors contract regarding an application layer.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public interface IApplicationService extends IWriteModel {

}
