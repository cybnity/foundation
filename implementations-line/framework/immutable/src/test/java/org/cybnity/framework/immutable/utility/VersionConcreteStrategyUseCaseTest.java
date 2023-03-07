package org.cybnity.framework.immutable.utility;

import static org.junit.Assert.assertNotNull;

import org.cybnity.framework.immutable.sample.ChildAggregate;
import org.cybnity.framework.immutable.sample.Employee;
import org.junit.Test;

/**
 * Unit test of VersionConcreteStrategy behaviors regarding its immutability
 * supported requirements.
 * 
 * @author olivier
 *
 */
public class VersionConcreteStrategyUseCaseTest {

    @Test
    public void givenSimpleClass_whenNoneInherentFields_thenValidVersionHashDelivered() throws Exception {
	String versionHash = new VersionConcreteStrategy().composeCanonicalVersionHash(Employee.class);
	assertNotNull(versionHash); // In case of NoSuchAlgorithmException resulting of missing MessageDigest
				    // algorithm
    }

    @Test
    public void giveInherentClass_whenSuperClassFields_thenVersionHashIncludingInherentAttributes() throws Exception {
	String versionHash = new VersionConcreteStrategy().composeCanonicalVersionHash(ChildAggregate.class);
	assertNotNull(versionHash); // In case of NoSuchAlgorithmException resulting of missing MessageDigest
				    // algorithm
    }
}
