package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import org.cybnity.framework.domain.IDescribed;
import org.cybnity.framework.domain.event.CorrelationIdFactory;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.ChannelObserver;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MappingException;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MessageMapper;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.StreamObserver;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Task ensuring independent observation of a Redis topic in a permanent way.
 * Listen on channel messages (with optional pattern applied for messages selection) that receive all inbound messages.
 * The observation stops triggering events when the subscriber unsubscribes from it.
 */
public class ChannelObservationTask implements Callable<Void> {

    private final RedisClient client;
    private final ChannelObserver delegate;
    /**
     * Technical logging
     */
    private final Logger logger = Logger.getLogger(ChannelObservationTask.class.getName());

    private final MessageMapper mapper;

    /**
     * Default constructor.
     *
     * @param client           Mandatory client allowing connection to the channel provider (e.g UIS server).
     * @param delegateToNotify Mandatory delegate to notify when received message from the observed channel.
     * @param eventMapper      Mandatory message mapper allowing read of messages and their transformation to event types.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public ChannelObservationTask(RedisClient client, ChannelObserver delegateToNotify, MessageMapper eventMapper) throws IllegalArgumentException {
        if (client == null) throw new IllegalArgumentException("Client parameter is required!");
        if (delegateToNotify == null) throw new IllegalArgumentException("DelegateToNotify parameter is required!");
        if (eventMapper == null) throw new IllegalArgumentException("Event mapper parameter is required!");
        this.client = client;
        this.delegate = delegateToNotify;
        this.mapper = eventMapper;
    }

    /**
     * Get the delegate which is called for each message received.
     *
     * @return A delegate instance.
     */
    public ChannelObserver getDelegateObserver() {
        return this.delegate;
    }

    /**
     * See <a href="https://github.com/lettuce-io/lettuce-core/wiki/Pub-Sub">Lettuce documentation</a> for more details about this implementation
     */
    @Override
    public Void call() throws Exception {
        // --- LETTUCE DOC ---
        // User-space Pub/Sub messages (Calling PUBLISH) are broadcasted across the whole cluster regardless of subscriptions to particular channels/patterns. This behavior allows connecting to an arbitrary cluster node and registering a subscription. The client isn’t required to connect to the node where messages were published.
        // Use Redis Cluster mode for Pub/Sub support
        // See https://github.com/lettuce-io/lettuce-core/wiki/Pub-Sub for more Lettuce client detail

        // --- REDIS DOC ---
        // Carefully and currently, Redis Cluster does not support NATted environments and in general environments where IP addresses or TCP ports are remapped.
        // Docker uses a technique called port mapping: programs running inside Docker containers may be exposed with a different port compared to the one the program believes to be using. This is useful for running multiple containers using the same ports, at the same time, in the same server.
        // To make Docker compatible with Redis Cluster, you need to use Docker's host networking mode. Please see the --net=host option in the Docker documentation for more information.
        // See https://redis.io/docs/management/scaling/ for more Redis detail
        // Sharded Pub/Sub documentation at https://redis.io/docs/interact/pubsub/
        // ------------------

        // TODO Change current standard Topic implementation (that is not observing clusterized redis nodes' topics) for Redis Cluster usage

        // Identify the path to the observed channel
        String topicPathName = this.delegate.observed().name();

        // Identify observation pattern optionally defined
        // When existing pattern, it shall be added to the observed channel name as specialization of the observed topic (e.g <<channel name>><<pattern>>)
        String observationPattern = this.delegate.observationPattern();
        if (observationPattern != null && !observationPattern.isEmpty()) {
            // Add pattern to the channel name observed
            topicPathName += observationPattern;
        }

        // Initialize a connection to the space
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        // Prepare observation delegation
        connection.addListener(new RedisPubSubListener<>() {
            /**
             * Message received from a channel subscription.
             *
             * @param channel Channel
             * @param message Message
             */
            @Override
            public void message(String channel, String message) {
                if (message != null && !message.isEmpty()) {
                    // Process the message
                    processMessage(message);
                }
            }

            /**
             * Message received from a pattern subscription
             *
             * @param pattern Pattern
             * @param channel Channel
             * @param message Message
             */
            @Override
            public void message(String pattern, String channel, String message) {
                if (message != null && !message.isEmpty()) {
                    // Process the message
                    processMessage(message);
                }
            }

            /**
             * Common message treatment rules to perform with delegation to delegate.
             *
             * @param message Mandatory origin message to treat as received event.
             * @throws IllegalArgumentException When mandatory parameter is not defined.
             */
            private void processMessage(String message) throws IllegalArgumentException {
                if (message == null || message.isEmpty())
                    throw new IllegalArgumentException("Message parameter is required!");

                try {
                    // Transform event into supported message type
                    mapper.transform(message);
                    IDescribed event = (IDescribed) mapper.getResult();

                    // Transmit collected event to the observer
                    delegate.notify(event);
                } catch (MappingException mape) {
                    logger.log(Level.SEVERE, "Invalid message type collected from " + delegate.observed().name() + " channel!", mape);
                }
            }

            @Override
            public void subscribed(String channel, long count) {
                logger.info(
                        "Observation task is waiting for new message from channel (" + channel + ")"
                );
            }

            @Override
            public void psubscribed(String pattern, long count) {
                logger.info(
                        "Observation task is waiting for new message from channel pattern (" + pattern + ")"
                );
            }

            @Override
            public void unsubscribed(String channel, long count) {
                logger.info(
                        "Observation task is stopped regarding channel (" + channel + ")"
                );
            }

            @Override
            public void punsubscribed(String pattern, long count) {
                logger.info(
                        "Observation task is stopped regarding channel pattern (" + pattern + ")"
                );
            }
        });

        // Subscribe to channel
        // Asynchronous subscription mode (avoid blocking of EventLoop by synchronous API call of Lettuce; allow to fetch data from Redis from inside the observer callback)
        RedisPubSubAsyncCommands<String, String> async = connection.async();
        RedisFuture<Void> f = async.subscribe(topicPathName);
        return f.get();
    }
}