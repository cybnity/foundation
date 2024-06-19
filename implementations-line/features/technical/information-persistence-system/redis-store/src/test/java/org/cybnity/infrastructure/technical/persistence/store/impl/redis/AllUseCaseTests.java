package org.cybnity.infrastructure.technical.persistence.store.impl.redis;

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
@SelectClasses({UISPersistentStreamAdapterUseCaseTest.class})
public class AllUseCaseTests {
}