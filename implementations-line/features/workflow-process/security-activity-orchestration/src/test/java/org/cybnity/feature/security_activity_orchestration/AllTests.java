package org.cybnity.feature.security_activity_orchestration;

import org.cybnity.feature.security_activity_orchestration.templating.domain.model.ProcessDescriptorUseCaseTest;
import org.cybnity.feature.security_activity_orchestration.templating.domain.model.ProcessUseCaseTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Suite of all technical and behavior tests regarding the domain capabilities.
 * 
 * @author olivier
 *
 */
@Suite
@SelectClasses({ ProcessDescriptorUseCaseTest.class, ProcessUseCaseTest.class })
public class AllTests {
}
