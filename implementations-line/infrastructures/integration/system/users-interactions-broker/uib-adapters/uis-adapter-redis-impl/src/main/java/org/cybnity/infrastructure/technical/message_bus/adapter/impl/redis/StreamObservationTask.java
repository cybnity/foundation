package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.cybnity.framework.domain.IDescribed;
import org.cybnity.framework.domain.event.CorrelationIdFactory;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MappingException;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MessageMapper;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.StreamObserver;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Task ensuring independent observation of a Redis stream in a permanent way.
 * Listen on channel messages (with optional pattern applied for messages selection) that receive all inbound messages.
 * The observation stops triggering events when the subscriber unsubscribes from it.
 */
public class StreamObservationTask implements Callable<Void> {

    private final RedisClient client;
    private final StreamObserver delegate;
    private final String consumersGroupName;
    /**
     * Technical logging
     */
    private final Logger logger = Logger.getLogger(StreamObservationTask.class.getName());

    private final MessageMapper mapper;

    /**
     * Default constructor.
     *
     * @param client           Mandatory client allowing connection to the stream provider (e.g UIS server).
     * @param delegateToNotify Mandatory delegate to notify when received message from the observed stream.
     * @param eventMapper      Mandatory message mapper allowing read of messages and their transformation to event types.
     * @throws IllegalArgumentException When mandatory parameter is missing. When the consumer group name of the delegate to notify is not defined.
     */
    public StreamObservationTask(RedisClient client, StreamObserver delegateToNotify, MessageMapper eventMapper) throws IllegalArgumentException {
        if (client == null) throw new IllegalArgumentException("Client parameter is required!");
        if (delegateToNotify == null) throw new IllegalArgumentException("DelegateToNotify parameter is required!");
        // Control the presence of consumer group name that is mandatory for streams listening
        if (/* Optionally defined */ delegateToNotify.consumerGroupName() == null || delegateToNotify.consumerGroupName().isEmpty())
            throw new IllegalArgumentException("Consumer group name is required!");
        if (eventMapper == null) throw new IllegalArgumentException("Event mapper parameter is required!");
        this.client = client;
        this.delegate = delegateToNotify;
        this.consumersGroupName = delegate.consumerGroupName();
        this.mapper = eventMapper;
    }

    /**
     * Get the delegate which is called for each message received.
     *
     * @return A delegate instance.
     */
    public StreamObserver getDelegateObserver() {
        return this.delegate;
    }

    @Override
    public Void call() throws Exception {
        // Identify the path to the observed stream
        String streamPathName = this.delegate.observed().name();

        // TODO Change current standard Stream implementation (that is not observing clusterized redis nodes' topics) for Redis Cluster usage
        // For example, when a stream in not created on the same Redis node than the observes, some event could be not received

        // Initialize a connection to the space
        StatefulRedisConnection<String, String> connection = this.client.connect();
        final RedisCommands<String, String> syncCommands = connection.sync();

        if (consumersGroupName != null && !consumersGroupName.isEmpty()) {
            // Create consumer group when not existing
            try {
                // Create a group of stream listeners
                String observationPattern = this.delegate.observationPattern();
                syncCommands.xgroupCreate(XReadArgs.StreamOffset.from(streamPathName, (observationPattern != null && !observationPattern.isEmpty()) ? observationPattern : StreamObserver.DEFAULT_OBSERVATION_PATTERN), consumersGroupName, XGroupCreateArgs.Builder.mkstream(true));
            } catch (Exception redisBusyException) {
                logger.info(String.format("Group '%s' already exists", consumersGroupName));
            }
        } else {
            throw new Exception("A consumers group name need to be defined by the delegate as mandatory for stream messages observation!");
        }

        // Define a technical name of the consumer instance
        String consumerInstanceName = CorrelationIdFactory.generate(String.valueOf(this.delegate.hashCode()));
        this.logger.info(
                "Observation task (consumer name: " + consumerInstanceName +
                        ", consumer group: " + consumersGroupName + ") is waiting for new message from stream (" + streamPathName + ")"
        );

        while (true) {
            try {
                List<StreamMessage<String, String>> messages = syncCommands.xreadgroup(
                        Consumer.from(consumersGroupName, consumerInstanceName),
                        XReadArgs.StreamOffset.lastConsumed(streamPathName)
                );
                if (!messages.isEmpty()) {
                    // Prepare a mapper supporting messages transformation
                    IDescribed event;
                    for (StreamMessage<String, String> message : messages) {
                        if (message != null) {
                            try {
                                // Transform event into supported message type
                                mapper.transform(message);
                                event = (IDescribed) mapper.getResult();
                                // Transmit collected event to the observer
                                this.delegate.notify(event);

                                // Confirm that the message has been processed using XACK
                                syncCommands.xack(streamPathName, consumersGroupName, message.getId());
                            } catch (MappingException mape) {
                                logger.log(Level.SEVERE, "Invalid message type collected from " + this.delegate.observed().name() + " stream!", mape);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Temporary connection close/re-opening can generate a temporary exception for connection close status
                //logger.log(Level.SEVERE, "Problem during stream message read from group!", e.getMessage());
            }
        }
    }
}