package org.cybnity.infrastructure.technical.message_bus.adapter.impl;

import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.ExecutableAdapterCheckerUseCaseTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Suite of all technical and behavior unit tests regarding the implementation
 * components capabilities without need of platform.
 * 
 * @author olivier
 *
 */
@Suite
@SelectClasses({ ExecutableAdapterCheckerUseCaseTest.class })
public class AllUseCaseTests {
}