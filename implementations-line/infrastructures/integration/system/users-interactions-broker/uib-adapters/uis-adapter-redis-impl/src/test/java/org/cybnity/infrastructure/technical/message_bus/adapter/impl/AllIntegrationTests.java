package org.cybnity.infrastructure.technical.message_bus.adapter.impl;

import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.RedisOptionFactoryDeployedSystemIntegration;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Suite of all technical and behavior tests regarding the implementation
 * components capabilities requiring platform component (e.g Redis server
 * running).
 * 
 * @author olivier
 *
 */
@SelectClasses({ RedisOptionFactoryDeployedSystemIntegration.class })
@Suite
public class AllIntegrationTests {
}