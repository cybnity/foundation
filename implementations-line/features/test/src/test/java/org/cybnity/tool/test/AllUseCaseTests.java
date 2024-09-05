package org.cybnity.tool.test;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Suite of all technical and behavior unit tests regarding the implementation
 * components capabilities without need of platform.
 *
 * @author olivier
 */
@Suite
@SelectClasses({RedisOnlyServerInfrastructureUseCaseTest.class, RedisAndJanusGraphOnlyServersInfrastructureUseCaseTest.class, AllServersInfrastructureUseCaseTest.class})
public class AllUseCaseTests {

}