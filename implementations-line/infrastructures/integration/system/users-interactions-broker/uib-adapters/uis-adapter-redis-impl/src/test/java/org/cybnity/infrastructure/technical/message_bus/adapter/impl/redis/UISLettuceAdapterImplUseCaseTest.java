package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.event.CommandFactory;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.Stream;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.UISAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * Test and check the instantiation of the adapter implementation reusing the Lettuce library and settings.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UISLettuceAdapterImplUseCaseTest extends ContextualizedRedisActiveTestContainer {

    /**
     * This test is creating an adapter instance and validate its operational usage according to the current configuration.
     */
    @Test
    public void givenRedisSpaceStarted_whenClientConfigurationDefined_thenAdapterInstantiatedWithSuccess() throws Exception {
        // Try adapter instance creation with automatic configuration of Lettuce client
        // from a valid context settings set
        UISAdapter adapter = new UISAdapterImpl(getContext());
    }

    /**
     * This test try to push a command event into a dedicated stream via adapter, and check that event is stored by Redis.
     * This scenario validate that without defined stream recipient during the call, the adapter automatically detect the recipient from the original event attribute and finalize the space feeding with success.
     * The origin event pushed to space is anonymous (does not be identified by uid) in this test scenario, to validate the support of anonymous command to Redis streams.
     */
    @Test
    public void givenAnonymousCommandIncludeDomainEntryPoint_whenPushedToSpaceWithoutRecipientDefined_thenStoredInStreamDetectedFromCommand() throws Exception {
        // --- COMMAND TO ENTRYPOINT ---
        Collection<Attribute> definition = new ArrayList<>();
        // Prepare command event sample
        // Set organization name
        definition.add(new Attribute("OrganizationNaming", "CYBNITY"));
        // Set included specification attribute about root channel allowing automatic detection by adapter of stream recipient
        definition.add(new Attribute(Stream.Specification.STREAM_ENTRYPOINT_PATH_NAME.name(), /* Example of global entrypoint of Access Control capability domain */ "ac"));

        // Build sample ANONYMOUS command event relative to capability to execute (e.g capability domain supported feature)
        Command requestEvent = CommandFactory.create("REGISTER_ORGANIZATION",
                /* No identified as anonymous transaction without correlation id need*/ null, definition,
                /* none prior command to reference*/ null,
                /* None pre-identified organization because new creation */ null);
        // Auto-assign correlation identifier allowing finalized transaction check
        requestEvent.generateCorrelationId(null);

        // Initialize an adapter connected to contextualized Redis server (Users Interactions Space)
        UISAdapter adapter = new UISAdapterImpl(getContext());

        // Execute command via adapter (WITH AUTO-DETECTION OF STREAM RECIPIENT FROM REQUEST EVENT)
        String messageId = adapter.append(requestEvent, (Stream) null /* None defined stream simulating auto-detection by adapter from the event's embedded specification (STREAM_ENTRYPOINT_PATH_NAME) */);

        // Check that message was appended with success
        Assertions.assertNotNull(messageId);
    }

    /**
     * This test try to push a command event into a dedicated stream via adapter, and check that event is stored by Redis.
     * This scenario validate that when targeted stream recipient is defined via parameter of the method call, the adapter select the defined recipient (and ignore potential original event attribute including also an attribute relative to a entrypoint path name) and finalize the space feeding with success.
     * The origin event pushed to space is specifically identifier in this test scenario, to validate the support of automatic generation of combined message identifier including the origin command's id.
     */
    @Test
    public void givenIdentifiedCommand_whenPushedToSpaceDefinedRecipient_thenStoredInStream() throws Exception {
        // --- COMMAND TO ENTRYPOINT ---
        Collection<Attribute> definition = new ArrayList<>();
        // Prepare command event sample
        // Set organization name
        definition.add(new Attribute("OrganizationNaming", "CYBNITY"));
        // Don't specify any attribute about root channel allowing automatic detection by adapter of stream recipient

        // Build sample command event relative to capability to execute (e.g capability domain supported feature)
        // that is IDENTIFIED as original event
        DomainEntity commandUID = new DomainEntity(new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(),
                /* identifier as performed transaction number */ UUID.randomUUID().toString()));

        Command requestEvent = CommandFactory.create("REGISTER_ORGANIZATION",
                /* Command event identified by */ commandUID, definition,
                /* none prior command to reference*/ null,
                /* None pre-identified organization because new creation */ null);
        // Auto-assign correlation identifier allowing finalized transaction check
        requestEvent.generateCorrelationId(null);

        // Define stream endpoint to feed
        Stream recipient = new Stream(/* Example of global entrypoint of Access Control capability domain */ "ac");

        // Initialize an adapter connected to contextualized Redis server (Users Interactions Space)
        UISAdapter adapter = new UISAdapterImpl(getContext());

        // Execute command via adapter (WITH AUTO-DETECTION OF STREAM RECIPIENT FROM REQUEST EVENT)
        String messageId = adapter.append(requestEvent, recipient /* Specific stream to feed */);

        // Check that message was appended with success
        Assertions.assertNotNull(messageId);
    }

}
