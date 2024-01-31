package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.IDescribed;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.*;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * Implementation class of the adapter client to the Users Interactions Space
 * provided via a Redis server. It's a client library supporting the
 * interactions with the Redis solution, and that encapsulate the utility
 * components (e.g specific Lettuce Redis client implementation).
 * <p>
 * For help about Lettuce client usage, see <a href="https://www.baeldung.com/java-redis-lettuce">Redis with Lettuce tutorial</a>
 *
 * @author olivier
 */
public class UISAdapterImpl implements UISAdapter {

    /**
     * Technical logging
     */
    private static final Logger logger = Logger.getLogger(UISAdapterImpl.class.getName());

    /**
     * Current context of adapter runtime.
     */
    private final IContext context;

    /**
     * Utility class managing the verification of operable adapter instance.
     */
    private ExecutableAdapterChecker healthyChecker;

    /**
     * Client proxy instance to the space.
     */
    private RedisClient client;

    private Duration connectionTimeout;

    /**
     * Pool of standalone threads registered and executing the streams listening tasks.
     */
    private final ExecutorService currentStreamObserversPool = Executors.newCachedThreadPool();

    /**
     * Pool of standalone threads registered and executing the channels listening tasks.
     */
    private final ExecutorService currentChannelObserversPool = Executors.newCachedThreadPool();

    /**
     * Concurrent accessible set of started futures regarding registered stream observations.
     * Key = stream path name, Value = started thread.
     * Concurrent access shall be supported regarding map read and upgrade of managed threads.
     * Several threads can acquire locks on different map segments, so multiple threads can access the Map at the same time.
     */
    private final Map<StreamObserver, Future<Void>> currentStreamObserversThreads = new ConcurrentHashMap<>();

    /**
     * Concurrent accessible set of started futures regarding registered channel observations.
     * Key = channel path name, Value = started thread.
     * Concurrent access shall be supported regarding map read and upgrade of managed threads.
     * Several threads can acquire locks on different map segments, so multiple threads can access the Map at the same time.
     */
    private final Map<ChannelObserver, Future<Void>> currentChannelObserversThreads = new ConcurrentHashMap<>();

    /**
     * Default constructor of the adapter ready to manage interactions with the
     * Redis instance(s).
     *
     * @param context Mandatory context provider of reusable configuration allowing
     *                to connect to Redis instance(s).
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     * @throws UnoperationalStateException When any required environment variable is
     *                                     not defined or have not value ready for
     *                                     use.
     */
    public UISAdapterImpl(IContext context) throws IllegalArgumentException, UnoperationalStateException {
        if (context == null)
            throw new IllegalArgumentException("Context parameter is required!");
        this.context = context;

        // Check the minimum required data allowing connection to the targeted Redis
        // server
        checkHealthyState();
    }

    /**
     * Get a singleton instance of client.
     * When previous and initial instance have been removed (e.g over freeUpResources() call), this method re-instantiate a singleton instance and return it.
     *
     * @return A singleton instance of Redis client.
     * @throws UnoperationalStateException When system access via uri is in failure.
     */
    private RedisClient getClient() throws UnoperationalStateException {
        if (this.client == null) {
            // Initialize new singleton instance
            try {
                // Prepare the Redis option allowing discussions with the Users Interactions Space
                RedisURI writeModelURI = RedisURIFactory.createUISWriteModelURI(this.context);
                // Create Redis Lettuce client
                this.client = RedisClient.create(writeModelURI);
            } catch (SecurityException s) {
                throw new UnoperationalStateException(s);
            }
        }
        return this.client;
    }

    @Override
    public void freeUpResources() {
        try {
            if (getClient() != null) {
                // Remove any existing listeners managed by this client
                unregister(currentStreamObserversThreads.keySet());
                unsubscribe(currentChannelObserversThreads.keySet());

                // Disconnect client from space
                getClient().shutdown();
                // remove singleton instance which is not usable
                this.client = null;
            }
        } catch (UnoperationalStateException e) {
            logger.severe(e.getMessage());
        }
    }

    @Override
    public void checkHealthyState() throws UnoperationalStateException {
        if (healthyChecker == null)
            healthyChecker = new ExecutableAdapterChecker(context);
        // Execution the health check
        healthyChecker.checkOperableState();
    }

    @Override
    public void register(Collection<StreamObserver> observers, MessageMapper eventMapper) throws IllegalArgumentException, UnoperationalStateException {
        if (observers != null && !observers.isEmpty()) {
            for (StreamObserver listener : observers) {
                // Verify that a same observer is not already existing for listening of the same topic (avoiding multiple registration of a same observer)
                boolean alreadyObservedStreamOverEqualsPattern = false;
                for (Map.Entry<StreamObserver, Future<Void>> item : currentStreamObserversThreads.entrySet()) {
                    if (item.getKey().equals(listener)) {
                        alreadyObservedStreamOverEqualsPattern = true;
                        break; // Stop search
                    }
                }
                if (!alreadyObservedStreamOverEqualsPattern) {
                    Future<Void> f = currentStreamObserversPool.submit(new StreamObservationTask(getClient(), listener, eventMapper));
                    // Get handle to the started thread for potential future stop
                    currentStreamObserversThreads.put(listener, f);
                }
            }
        }
    }

    @Override
    public void subscribe(Collection<ChannelObserver> observers, MessageMapper eventMapper) throws IllegalArgumentException, UnoperationalStateException {
        if (observers != null && !observers.isEmpty()) {
            for (ChannelObserver listener : observers) {
                // Verify that a same observer is not already existing for listening of the same topic (avoiding multiple registration of a same observer)
                boolean alreadyObservedChannelOverEqualsPattern = false;
                for (Map.Entry<ChannelObserver, Future<Void>> item : currentChannelObserversThreads.entrySet()) {
                    if (item.getKey().equals(listener)) {
                        alreadyObservedChannelOverEqualsPattern = true;
                        break; // Stop search
                    }
                }
                if (!alreadyObservedChannelOverEqualsPattern) {
                    Future<Void> f = currentChannelObserversPool.submit(new ChannelObservationTask(getClient(), listener, eventMapper));
                    // Get handle to the started thread for potential future stop
                    currentChannelObserversThreads.put(listener, f);
                }
            }
        }
    }

    @Override
    public void unregister(Collection<StreamObserver> observers) {
        if (observers != null) {
            for (StreamObserver listener : observers) {
                // Find previously registered observation tasks registry based on logical equals
                Map<StreamObserver, Future<Void>> copy = new HashMap<>(currentStreamObserversThreads);

                for (Map.Entry<StreamObserver, Future<Void>> item : copy.entrySet()) {
                    if (item.getKey().equals(listener)) {
                        // Existing equals observer reference which can be interrupted
                        Future<Void> thread = item.getValue();
                        if (thread != null) {
                            // Interrupt the task
                            if (thread.cancel(true)) {
                                // Clean container of thread regarding the previous instance
                                currentStreamObserversThreads.remove(item.getKey());
                                logger.fine("Observer of stream (" + listener.observed().name() + ") is stopped");
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void unsubscribe(Collection<ChannelObserver> observers) {
        if (observers != null) {
            for (ChannelObserver listener : observers) {
                // Find previously registered observation tasks registry based on logical equals
                Map<ChannelObserver, Future<Void>> copy = new HashMap<>(currentChannelObserversThreads);

                for (Map.Entry<ChannelObserver, Future<Void>> item : copy.entrySet()) {
                    if (item.getKey().equals(listener)) {
                        // Existing equals observer reference which can be interrupted
                        Future<Void> thread = item.getValue();
                        if (thread != null) {
                            // Interrupt the task
                            if (thread.cancel(true)) {
                                // Clean container of thread regarding the previous instance
                                currentChannelObserversThreads.remove(item.getKey());
                                logger.fine("Observer of channel (" + listener.observed().name() + ") is stopped");
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public Collection<Channel> activeChannels(String namingFilter) {
        throw new IllegalArgumentException("ADAPTER IMPL SERVICE TO IMPLEMENT!");
    }

    @Override
    public List<String> append(IDescribed event, List<Stream> recipients, MessageMapper eventMapper) throws IllegalArgumentException, MappingException, UnoperationalStateException {
        if (event == null) throw new IllegalArgumentException("Event parameter is required!");
        if (eventMapper == null) throw new IllegalArgumentException("Event mapper parameter is required!");
        if (recipients == null) throw new IllegalArgumentException("Recipients parameter is required!");
        if (recipients.isEmpty())
            throw new IllegalArgumentException("Recipients parameter shall include minimum one recipient!");
        List<String> messageIdentifiers = new LinkedList<>();
        // Append the event on each recipient
        for (Stream s : recipients) {
            messageIdentifiers.add(this.append(event, s, eventMapper));
        }
        return messageIdentifiers;
    }

    @Override
    public String append(IDescribed factEvent, Stream recipient, MessageMapper eventMapper) throws IllegalArgumentException, MappingException, UnoperationalStateException {
        if (factEvent == null) throw new IllegalArgumentException("factEvent parameter is required!");
        if (eventMapper == null) throw new IllegalArgumentException("Event mapper parameter is required!");
        String recipientPathName = null;
        String messageId = null;
        if (recipient == null) {
            // Detect potential defined recipient stream name from event
            for (Attribute specification : factEvent.specification()) {
                if (Stream.Specification.STREAM_ENTRYPOINT_PATH_NAME.name().equals(specification.name())) {
                    recipientPathName = specification.value();
                    break; // Stop search
                }
            }
        } else {
            recipientPathName = recipient.name();
        }
        if (recipientPathName == null || recipientPathName.isEmpty())
            throw new IllegalArgumentException("Recipient stream name not defined. Impossible push of factEvent on the space!");
        StatefulRedisConnection<String, String> connection = null;
        try {
            // Transform event into supported message type
            eventMapper.transform(factEvent);
            Map<String, String> messageBody = (Map<String, String>) eventMapper.getResult();

            // Send event to identified stream recipient
            connection = getClient().connect();
            RedisCommands<String, String> syncCommands = connection.sync();
            messageId = syncCommands.xadd(/* recipient name to feed */ recipientPathName, /* fact record transformed */ messageBody);
        } catch (ClassCastException cce) {
            // result cast problem
            throw new MappingException(cce);
        } finally {
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        }
        return messageId;
    }

    @Override
    public void publish(IDescribed event, Channel recipient, MessageMapper eventMapper) throws IllegalArgumentException, MappingException, UnoperationalStateException {
        if (event == null) throw new IllegalArgumentException("Event parameter is required!");
        if (eventMapper == null) throw new IllegalArgumentException("Event mapper parameter is required!");
        String recipientPathName = null;
        String messageId = null;
        if (recipient == null) {
            // Detect potential defined recipient channel name from event
            for (Attribute specification : event.specification()) {
                if (Channel.Specification.CHANNEL_ENTRYPOINT_PATH_NAME.name().equals(specification.name())) {
                    recipientPathName = specification.value();
                    break; // Stop search
                }
            }
        } else {
            recipientPathName = recipient.name();
        }
        if (recipientPathName == null || recipientPathName.isEmpty())
            throw new IllegalArgumentException("Recipient channel name not defined. Impossible publish of event on the space!");

        StatefulRedisPubSubConnection<String, String> connection = null;
        try {
            // Transform event into supported message type
            eventMapper.transform(event);
            String messageBody = (String) eventMapper.getResult();

            // Send event to identified channel recipient
            connection = getClient().connectPubSub();
            RedisPubSubAsyncCommands<String, String> asyncCommands = connection.async();
            asyncCommands.publish(/* recipient name to feed */ recipientPathName, /* fact record transformed */ messageBody);
        } catch (ClassCastException cce) {
            // result cast problem
            throw new MappingException(cce);
        } finally {
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        }
    }

    @Override
    public void publish(IDescribed event, Collection<Channel> recipients, MessageMapper eventMapper) throws IllegalArgumentException, MappingException, UnoperationalStateException {
        if (event == null) throw new IllegalArgumentException("Event parameter is required!");
        if (eventMapper == null) throw new IllegalArgumentException("Event mapper parameter is required!");
        if (recipients == null) throw new IllegalArgumentException("Recipients parameter is required!");
        if (recipients.isEmpty())
            throw new IllegalArgumentException("Recipients parameter shall include minimum one recipient!");
        // Publish the event on each recipient
        for (Channel s : recipients) {
            this.publish(event, s, eventMapper);
        }
    }

}
