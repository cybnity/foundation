package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Suite of all technical and behavior unit tests regarding the implementation
 * components capabilities without need of platform.
 *
 * @author olivier
 */
@Suite
@SelectClasses({GraphElementsManagementUseCaseTest.class, JanusGraphSchemaCreationUseCaseTest.class, ActivateGraphUseCaseTest.class})
public class AllUseCaseTests {
}