package org.cybnity.framework.application.vertx.common;

import org.cybnity.framework.application.vertx.common.routing.RouteRecipientListUseCaseTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Suite of all technical and behavior unit tests regarding the implementation
 * components capabilities without need of platform.
 *
 * @author olivier
 */
@Suite
@SelectClasses({RouteRecipientListUseCaseTest.class})
public class AllUseCaseTests {
}