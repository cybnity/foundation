package org.cybnity.infrastructure.technical.registry.adapter.impl;

import org.cybnity.infrastructure.technical.registry.adapter.impl.janusgraph.GraphElementsManagementUseCaseTest;
import org.cybnity.infrastructure.technical.registry.adapter.impl.janusgraph.JanusGraphSchemaCreationUseCaseTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Suite of all technical and behavior unit tests regarding the implementation
 * components capabilities without need of platform.
 *
 * @author olivier
 */
@Suite
@SelectClasses({GraphElementsManagementUseCaseTest.class, JanusGraphSchemaCreationUseCaseTest.class})
public class AllUseCaseTests {
}