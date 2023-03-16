package org.cybnity.infrastructure.technical.message_bus.adapter.impl;

import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.ExecutableAdapterCheckerUseCaseTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Suite of all technical and behavior unit tests regarding the implementation
 * components capabilities without need of platform.
 * 
 * @author olivier
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ ExecutableAdapterCheckerUseCaseTest.class })
public class AllUseCaseTests {
}