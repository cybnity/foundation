package org.cybnity.application.ui.system.backend;

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
@SelectClasses({ ExecutableBackendCheckerUseCaseTest.class, SockJSReactiveBackendServerTest.class })
public class AllUseCaseTests {
}