package org.cybnity.framework.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cybnity.framework.domain.event.CommandFactory;
import org.cybnity.framework.domain.model.DomainEntity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test regarding behavior of builder.
 */
public class ObjectMapperBuilderUseCaseTest {

    /**
     * Check that the default configuration of a mapper is creating JSON object with good default formatted contents.
     */
    @Test
    public void givenCommandEvent_whenCreateJsonVersion_thenStandardFormatAppliedOnObject() throws Exception {
        // Prepare json object (RegisterOrganization command event including organization naming)
        Collection<Attribute> definition = new ArrayList<>();
        // Set organization name
        Attribute tenantNameToRegister = new Attribute("OrganizationNaming", "CYBNITY");
        definition.add(tenantNameToRegister);

        // Define a command identifier allowing its identification
        DomainEntity cmdId = new DomainEntity(new IdentifierStringBased("id", UUID.randomUUID().toString()));

        // Create prior command reference
        Command priorCmd = CommandFactory.create("REGISTER_ORGANIZATION_PREVIOUS",
                cmdId,
                definition,
                /* none prior command to reference*/ null,
                /* None pre-identified organization because new creation */ null
        );

        cmdId = new DomainEntity(new IdentifierStringBased("id", UUID.randomUUID().toString()));
        Command event = CommandFactory.create("REGISTER_ORGANIZATION",
                cmdId,
                definition,
                /* none prior command to reference*/ priorCmd.reference(),
                /* None pre-identified organization because new creation */ null
        );

        // Create mapping in JSON object
        ObjectMapper mapper = new ObjectMapperBuilder()
                .enableIndentation()
                .dateFormat()
                .preserveOrder(true)
                .build();

        // Check serialized in JSON format
        String commandJson = mapper.writeValueAsString(event);
        System.out.println(commandJson);

        // Check each content serialized version
        assertTrue(commandJson.contains("occurredOn"), "Shall have been serialized");
        assertTrue(commandJson.contains("REGISTER_ORGANIZATION"), "Shall have been serialized");
        assertTrue(commandJson.contains("OrganizationNaming"), "Shall have been serialized");
        assertTrue(commandJson.contains("CYBNITY"), "Shall have been serialized");

        // Check deserializable object from serialized version
        // or via mapper.readValue(commandJson, ConcreteCommandEvent.class);
        Command version = mapper.readerFor(Command.class).readValue(commandJson);

        // Check that serialized/deserialized offset date time (occurredOn, createdAt attribute) are equals to original value (e.g zone id and offset guarantee)
        assertEquals(event.occurredAt(), version.occurredAt().withOffsetSameInstant(event.occurredAt().getOffset()), "Serialization shall have not changed the value!");

        // Check identified command is equals
        assertEquals(event, version, "Shall be same identified as the original serialized instance!");
    }
}
