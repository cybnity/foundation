package org.cybnity.infrastructure.technical.message_bus.adapter.impl;

import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.ContextualizedRedisOptionFactoryDeployedSystemIntegrationUseCaseTest;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.ExecutableAdapterCheckerUseCaseTest;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.UISLettuceAdapterImplUseCaseTest;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.UISStreamLettuceAdapterUseCaseTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Suite of all technical and behavior unit tests regarding the implementation
 * components capabilities without need of platform.
 * When Redis server is used during a test, an embedded Redis server is started.
 *
 * @author olivier
 */
@Suite
@SelectClasses({ExecutableAdapterCheckerUseCaseTest.class, ContextualizedRedisOptionFactoryDeployedSystemIntegrationUseCaseTest.class, UISLettuceAdapterImplUseCaseTest.class, UISStreamLettuceAdapterUseCaseTest.class, UISChannelLettuceAdapterUseCaseTestManual.class})
public class AllUseCaseTests {
}