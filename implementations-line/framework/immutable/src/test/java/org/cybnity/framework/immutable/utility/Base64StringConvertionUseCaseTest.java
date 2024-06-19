package org.cybnity.framework.immutable.utility;

import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.sample.ChildAggregate;
import org.cybnity.framework.immutable.sample.EntityImpl;
import org.cybnity.framework.immutable.sample.IdentifierImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * Unit test regarding serialization and deserialization helper.
 *
 * @author olivier
 */
public class Base64StringConvertionUseCaseTest {

    private Entity predecessor;
    private IdentifierImpl childId;

    @BeforeEach
    public void initParentEntitySample() {
        predecessor = new EntityImpl(new IdentifierImpl("uid", "alk8756"));
        childId = new IdentifierImpl("uid", "765GFGHJ");
    }

    @AfterEach
    public void deleteParentEntitySample() {
        predecessor = null;
        childId = null;
    }

    @Test
    public void givenAggregate_whenSerialization_thenBase64StringVersionGenerated() {
        ChildAggregate child = new ChildAggregate(predecessor, childId);
        // Try to convert into string version
        Optional<String> version = Base64StringConverter.convertToString(child);
        // Check converted value
        Assertions.assertNotNull(version.get(),"String value shall have been serialized!");
        // Try to re-instantiate object
        Optional<ChildAggregate> instance = Base64StringConverter.convertFrom(version.get());
        ChildAggregate restored = instance.get();
        // Check retrieved instance
        Assertions.assertEquals(child, restored, "Invalid deserialized version!");
    }
}
