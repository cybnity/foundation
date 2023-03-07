package org.cybnity.framework.immutable;

import org.cybnity.framework.immutable.utility.VersionConcreteStrategyUseCaseTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Suite of all technical and behavior tests regarding the immutability
 * capabilities.
 * 
 * @author olivier
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ EntityUseCaseTest.class, ChildFactUseCaseTest.class, MembershipUseCaseTest.class,
	MutablePropertyUseCaseTest.class, VersionConcreteStrategyUseCaseTest.class })
public class AllTests {
}