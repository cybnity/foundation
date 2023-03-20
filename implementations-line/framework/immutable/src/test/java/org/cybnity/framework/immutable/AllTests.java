package org.cybnity.framework.immutable;

import org.cybnity.framework.immutable.persistence.FactEdgeUseCaseTest;
import org.cybnity.framework.immutable.persistence.FactRecordUseCaseTest;
import org.cybnity.framework.immutable.persistence.FactTypeUseCaseTest;
import org.cybnity.framework.immutable.persistence.RelationRoleUseCaseTest;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategyUseCaseTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Suite of all technical and behavior tests regarding the immutability
 * capabilities.
 * 
 * @author olivier
 *
 */
@Suite
@SelectClasses({ EntityUseCaseTest.class, ChildFactUseCaseTest.class, MembershipUseCaseTest.class,
	MutablePropertyUseCaseTest.class, VersionConcreteStrategyUseCaseTest.class, FactTypeUseCaseTest.class,
	FactRecordUseCaseTest.class, RelationRoleUseCaseTest.class, FactEdgeUseCaseTest.class })
public class AllTests {
}
