package org.cybnity.feature.security_activity_orchestration;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Suite of all technical and behavior tests regarding the domain capabilities.
 * 
 * @author olivier
 *
 */
@Suite
@SelectClasses({ ProcessDescriptorUseCaseTest.class, ProcessUseCaseTest.class,
		ProcessChangeDescriptionUseCaseTest.class, ProcessChangeActivationUseCaseTest.class,
		ProcessChangeCompletionUseCaseTest.class, ProcessChangeStagingUseCaseTest.class,
		ProcessBuilderUseCaseTest.class })
public class AllTests {
}
