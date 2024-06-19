package org.cybnity.framework.immutable.utility;

import org.cybnity.framework.immutable.sample.ChildAggregate;
import org.cybnity.framework.immutable.sample.Employee;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Test
    public void giveClassType_whenMultipleVersionGenerating_thenUniqueSameVersionHash() throws Exception {
	String versionHash = new VersionConcreteStrategy().composeCanonicalVersionHash(ChildAggregate.class);
	String versionHash2 = new VersionConcreteStrategy().composeCanonicalVersionHash(ChildAggregate.class);
	assertEquals(versionHash, versionHash2, "Shall not be different!");
    }
}
