package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import org.cybnity.framework.domain.*;
import org.cybnity.framework.domain.event.CollaborationEventType;
import org.cybnity.framework.domain.event.CorrelationIdFactory;
import org.cybnity.framework.domain.event.DomainEventFactory;
import org.cybnity.framework.domain.event.ProcessingUnitPresenceAnnounced;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Test and check the usage of Redis Pub/Sub via Lettuce adapter.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UISChannelLettuceAdapterUseCaseTest extends ContextualizedRedisActiveTestContainer {

    private final Logger logger = Logger.getLogger(UISChannelLettuceAdapterUseCaseTest.class.getName());

    /**
     * This test try to publish a domain event into a dedicated topic via adapter, and check that event is promoted by Redis.
     * This scenario validate that without defined topic recipient during the call, the adapter automatically detect the recipient from the original event attribute and finalize the space feeding with success.
     */
    @Test
    public void givenDomainEvent_whenPushedToSpaceDefinedRecipient_thenStoredInChannel() throws Exception {
        int qtyOfMessageToProcess = 2;

        // Define the registry of messages correlation identifiers to treat
        final Collection<String> messagesToProcess = new LinkedList<>();

        // Prepare domain event sample
        final String outputChannelName = "ac" + NamingConventions.CHANNEL_NAME_SEPARATOR + getClass().getSimpleName().toLowerCase();

        // Build sample domain events relative to capability to execute (e.g capability domain supported feature)
        List<DomainEvent> requestEvents = new LinkedList<>();
        boolean withIdentifier = false;
        for (int i = 0; i < qtyOfMessageToProcess; i++) {
            // --- EVENT TO ENTRYPOINT ---
            Collection<Attribute> definition = new ArrayList<>();
            // Set included specification attribute about root channel allowing automatic detection by adapter of topic recipient
            definition.add(new Attribute(Channel.Specification.CHANNEL_ENTRYPOINT_PATH_NAME.name(), /* Example of capability domain output channel */ outputChannelName));

            DomainEntity identifiedBy = null;
            if (withIdentifier) {
                identifiedBy = new DomainEntity(new IdentifierStringBased("id", UUID.randomUUID().toString()));
                withIdentifier = false;// Inverse for next loop
            } else {
                // As ANONYMOUS event
                withIdentifier = true;// Inverse for next loop
            }
            definition.add(new Attribute("ProcessingUnitName", "CYBNITY_PU_" + i));
            // Auto-assign correlation identifier allowing finalized transaction check
            definition.add(new Attribute(Command.CORRELATION_ID, CorrelationIdFactory.generate(/* event uid as salt */"CYBNITY_PU_" + i)));

            DomainEvent evt = DomainEventFactory.create("PROCESSING_UNIT_PRESENCE_ANNOUNCED",
                    identifiedBy, definition,
                    /* none prior event to reference*/ null,
                    /* None pre-identified domain event because new creation */ null);

            // Prepare announcing event
            ProcessingUnitPresenceAnnounced promotedEvent = new ProcessingUnitPresenceAnnounced(identifiedBy, CollaborationEventType.PROCESSING_UNIT_PRESENCE_ANNOUNCED);
            // Add specification attributes
            for (Attribute att : definition) {
                promotedEvent.appendSpecification(att);
            }

            // Add prepared message in registry to check at end of the test
            messagesToProcess.add(promotedEvent.correlationId().value());
            requestEvents.add(promotedEvent);
        }

        // Prepare helper for test time wait
        CountDownLatch waiter = new CountDownLatch(requestEvents.size() /* Quantity of message to wait about processing end confirmation */);

        // Initialize an adapter connected to contextualized Redis server (Users Interactions Space)
        final UISAdapter adapter = new UISAdapterImpl(getContext());

        Thread second = new Thread(() -> {
            // Simulate autonomous events push in topic
            try {
                MessageMapper mapper = new MessageMapperFactory().getMapper(IDescribed.class, String.class);
                // Execute each domain event via adapter (WITH AUTO-DETECTION OF CHANNEL RECIPIENT FROM REQUEST EVENT)
                for (DomainEvent requestEvt : requestEvents) {
                    adapter.publish(requestEvt, (Channel) null /* None defined channel simulating auto-detection by adapter from the event's embedded specification (STREAM_ENTRYPOINT_PATH_NAME) */, mapper);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Thread first = new Thread(() -> {
            // Simulate parallel consumers registration and channel observations
            try {
                Collection<ChannelObserver> observers = new ArrayList<>();

                // Simulate consumer observing a space output topic
                ChannelObserver listener = new ChannelObserverImpl() {

                    @Override
                    public Channel observed() {
                        return new Channel(outputChannelName);
                    }

                    @Override
                    public String observationPattern() {
                        return null; // None pattern after the channel name
                    }

                    @Override
                    public void notify(IDescribed event) {
                        String correlationId = (DomainEvent.class.isAssignableFrom(event.getClass())) ? ((DomainEvent) event).correlationId().value() : (Command.class.isAssignableFrom(event.getClass()) ? ((Command) event).correlationId().value() : null);
                        Assertions.assertNotNull(correlationId, "Shall exist regarding the original event collected!");
                        Assertions.assertFalse(correlationId.isEmpty(), "Correlation id value shall be originally defined!");
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

                // Register consumer and automatically create topic and consumers group
                // Define standard and common mapper usable regarding exchanged event types over the Redis pub/sub topic
                MessageMapper mapper = new MessageMapperFactory().getMapper(String.class, IDescribed.class);
                adapter.subscribe(observers, mapper);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        first.start();
        first.join();// will wait for first thread to finish, before starting second thread

        second.start();
        second.join();

        // Wait for give time to message to be processed
        Assertions.assertTrue(waiter.await(50, TimeUnit.SECONDS), "Timeout reached before messages treated!");// Wait confirmation of processed message before timeout

        // Check that all published messages (qtyOfMessageToProcess) had been treated by observers
        // messagesToProcess shall be empty (all prepared message correlation identifiers shall have been removed as processed with success by observer)
        Assertions.assertTrue(messagesToProcess.isEmpty(), "All correlation ids shall have been deleted as processed with success!");
        logger.info("All test messages processed with success");
    }

}