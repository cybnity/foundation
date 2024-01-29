package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import io.lettuce.core.StreamMessage;
import org.cybnity.framework.domain.*;
import org.cybnity.framework.domain.event.CommandFactory;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Test and check the usage of Redis stream via Lettuce adapter.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UISStreamLettuceAdapterUseCaseTest extends ContextualizedRedisActiveTestContainer {

    private final Logger logger = Logger.getLogger(UISStreamLettuceAdapterUseCaseTest.class.getName());

    private UISAdapter adapter;

    @AfterEach
    public void removeResources() {
        if (adapter != null)
            adapter.freeUpResources();
    }

    /**
     * This test try to push a command event into a dedicated stream via adapter, and check that event is stored by Redis.
     * This scenario validate that without defined stream recipient during the call, the adapter automatically detect the recipient from the original event attribute and finalize the space feeding with success.
     * The origin events pushed to space are sometime anonymous (does not be identified by uid) in this test scenario, to validate the support of anonymous command to Redis streams.
     */
    @Test
    public void givenAnonymousCommandIncludeDomainEntryPoint_whenPushedToSpaceWithoutRecipientDefined_thenStoredInStreamDetectedFromCommand() throws Exception {
        int qtyOfMessageToProcess = 3;

        // Define the registry of messages correlation identifiers to treat
        final Collection<String> messagesToProcess = new LinkedList<>();

        // Prepare command event sample
        final String entrypointName = "ac" + NamingConventions.STREAM_NAME_SEPARATOR + getClass().getSimpleName().toLowerCase();

        // Build sample command events relative to capability to execute (e.g capability domain supported feature)
        List<Command> requestEvents = new LinkedList<>();
        boolean withIdentifier = false;
        for (int i = 0; i < qtyOfMessageToProcess; i++) {
            // --- COMMAND TO ENTRYPOINT ---
            Collection<Attribute> definition = new ArrayList<>();
            // Set included specification attribute about root channel allowing automatic detection by adapter of stream recipient
            definition.add(new Attribute(Stream.Specification.STREAM_ENTRYPOINT_PATH_NAME.name(), /* Example of global entrypoint of Access Control capability domain */ entrypointName));

            // Set organization name dedicated to the request
            DomainEntity identifiedBy = null;
            if (withIdentifier) {
                identifiedBy = new DomainEntity(new IdentifierStringBased("id", UUID.randomUUID().toString()));
                withIdentifier = false;// Inverse for next loop
            } else {
                // As ANONYMOUS command
                withIdentifier = true;// Inverse for next loop
            }
            definition.add(new Attribute("OrganizationNaming", "CYBNITY_" + i));
            Command requestEvent = CommandFactory.create("REGISTER_ORGANIZATION",
                    identifiedBy, definition,
                    /* none prior command to reference*/ null,
                    /* None pre-identified organization because new creation */ null);
            // Auto-assign correlation identifier allowing finalized transaction check
            requestEvent.generateCorrelationId(null);

            // Add prepared message in registry to check at end of the test
            messagesToProcess.add(requestEvent.correlationId().value());
            requestEvents.add(requestEvent);
        }

        // Prepare helper for test time wait
        CountDownLatch waiter = new CountDownLatch(requestEvents.size() /* Quantity of message to wait about processing end confirmation */);

        // Initialize an adapter connected to contextualized Redis server (Users Interactions Space)
        adapter = new UISAdapterImpl(getContext());

        Thread first = new Thread(() -> {
            // Simulate parallel consumers registration and stream observations
            try {
                Collection<StreamObserver> observers = new ArrayList<>();

                // Simulate consumer observing a space entry point
                StreamObserver listener = new StreamObserverImpl() {

                    @Override
                    public Stream observed() {
                        return new Stream(entrypointName);
                    }

                    @Override
                    public String observationPattern() {
                        return StreamObserver.DEFAULT_OBSERVATION_PATTERN;
                    }

                    @Override
                    public String consumerGroupName() {
                        return UISStreamLettuceAdapterUseCaseTest.class.getSimpleName() + "-consumers";
                    }

                    @Override
                    public void notify(IDescribed event) {
                        String correlationId = (DomainEvent.class.isAssignableFrom(event.getClass())) ? ((DomainEvent) event).correlationId().value() : (Command.class.isAssignableFrom(event.getClass()) ? ((Command) event).correlationId().value() : null);
                        if (correlationId != null)
                            referenceProcessedMessageCorrelationId(correlationId);
                        waiter.countDown();
                    }

                    /**
                     * Save the registry of processed message correlation identifier.
                     * @param msgCorrelationId To remove from registry as processed with success.
                     */
                    private void referenceProcessedMessageCorrelationId(String msgCorrelationId) {
                        // Delete from original registry AS PROCESSED WITH SUCCESS
                        messagesToProcess.remove(msgCorrelationId);
                    }
                };
                observers.add(listener);

                // Register consumer and automatically create stream and consumers group
                // Define standard and common mapper usable regarding exchanged event types over the Redis stream
                MessageMapper mapper = new MessageMapperFactory().getMapper(StreamMessage.class, IDescribed.class);

                adapter.register(observers, mapper);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Thread second = new Thread(() -> {
            // Simulate autonomous events push in stream
            try {
                MessageMapper mapper = new MessageMapperFactory().getMapper(IDescribed.class, StreamMessage.class);
                // Execute each command via adapter (WITH AUTO-DETECTION OF STREAM RECIPIENT FROM REQUEST EVENT)
                for (Command requestEvt : requestEvents) {
                    String messageId = adapter.append(requestEvt, (Stream) null /* None defined stream simulating auto-detection by adapter from the event's embedded specification (STREAM_ENTRYPOINT_PATH_NAME) */, mapper);

                    // Check that message was appended with success
                    Assertions.assertNotNull(messageId);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        first.start();
        first.join();
        second.start();
        second.join();

        // Wait for give time to message to be processed
        Assertions.assertTrue(waiter.await(20, TimeUnit.SECONDS), "Timeout reached before messages treated!");// Wait confirmation of processed message before timeout

        // Check that all published messages (qtyOfMessageToProcess) had been treated by observers
        // messagesToProcess shall be empty (all prepared message correlation identifiers shall have been removed as processed with success by observer)
        Assertions.assertTrue(messagesToProcess.isEmpty(), "All correlation ids shall have been deleted as processed with success!");
        logger.info("All test messages processed with success");
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
        Stream recipient = new Stream(/* Example of global entrypoint of Access Control capability domain */ "ac" + NamingConventions.STREAM_NAME_SEPARATOR + getClass().getSimpleName().toLowerCase());

        // Initialize an adapter connected to contextualized Redis server (Users Interactions Space)
        adapter = new UISAdapterImpl(getContext());

        // Execute command via adapter (WITH AUTO-DETECTION OF STREAM RECIPIENT FROM REQUEST EVENT)
        MessageMapper mapper = new MessageMapperFactory().getMapper(IDescribed.class, StreamMessage.class);
        String messageId = adapter.append(requestEvent, recipient /* Specific stream to feed */, mapper);

        // Check that message was appended with success
        Assertions.assertNotNull(messageId);
    }

}