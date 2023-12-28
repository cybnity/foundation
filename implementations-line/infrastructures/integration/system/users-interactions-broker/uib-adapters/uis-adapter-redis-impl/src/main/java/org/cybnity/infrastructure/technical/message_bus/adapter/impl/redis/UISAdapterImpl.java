package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.IDescribed;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.*;

import java.time.Duration;
import java.util.*;
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
    private final RedisClient client;

    private Duration connectionTimeout;

    /**
     * Pool of standalone threads registered and executing the streams listening tasks.
     */
    private final ExecutorService currentStreamObserversPool = Executors.newCachedThreadPool();

    /**
     * Started future regarding registered stream observations.
     * Key = stream path name, Value = started thread.
     */
    private final Map<StreamObserver, Future<Void>> currentStreamObserversThreads = new HashMap<>();

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
        try {
            // Prepare the Redis option allowing discussions with the Users Interactions Space
            RedisURI writeModelURI = RedisURIFactory.createUISWriteModelURI(this.context);
            // Create Redis Lettuce client
            client = RedisClient.create(writeModelURI);
        } catch (SecurityException s) {
            throw new UnoperationalStateException(s);
        }
    }

    @Override
    public void freeUpResources() {
        if (client != null) {
            // Remove any existing listeners managed by this client
            unregister(currentStreamObserversThreads.keySet());

            // Disconnect client from space
            client.shutdown();
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
    public void register(Collection<StreamObserver> observers) throws IllegalArgumentException {
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
                    Future<Void> f = currentStreamObserversPool.submit(new StreamObservationTask(client, listener));
                    // Get handle to the started thread for potential future stop
                    currentStreamObserversThreads.put(listener, f);
                }
            }
        }
    }

    @Override
    public void subscribe(Collection<ChannelObserver> observers) throws IllegalArgumentException {
        throw new IllegalArgumentException("ADAPTER IMPL SERVICE TO IMPLEMENT!");
    }

    @Override
    public void unregister(Collection<StreamObserver> observers) {
        if (observers != null) {
            for (StreamObserver listener : observers) {
                // Find previously registered observation tasks registry based on logical equals
                for (Map.Entry<StreamObserver, Future<Void>> item : currentStreamObserversThreads.entrySet()) {
                    if (item.getKey().equals(listener)) {
                        // Existing equals observer reference which can be interrupted
                        Future<Void> thread = item.getValue();
                        if (thread != null) {
                            // Interrupt the task
                            thread.cancel(true);
                            // Clean container of threads regarding the previous instance reference
                            currentStreamObserversThreads.remove(item.getKey());
                            logger.info("Observer of stream (" + listener.observed().name() + ") is stopped");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void unsubscribe(Collection<ChannelObserver> observers) {
        throw new IllegalArgumentException("ADAPTER IMPL SERVICE TO IMPLEMENT!");
    }

    @Override
    public Collection<Channel> activeChannels(String namingFilter) {
        throw new IllegalArgumentException("ADAPTER IMPL SERVICE TO IMPLEMENT!");
    }

    @Override
    public List<String> append(IDescribed event, List<Stream> recipients) throws IllegalArgumentException, MappingException {
        if (event == null) throw new IllegalArgumentException("Event parameter is required!");
        if (recipients == null) throw new IllegalArgumentException("Recipients parameter is required!");
        if (recipients.isEmpty())
            throw new IllegalArgumentException("Recipients parameter shall include minimum one recipient!");
        List<String> messageIdentifiers = new LinkedList<>();
        // Append the event on each recipient
        for (Stream s : recipients) {
            messageIdentifiers.add(this.append(event, s));
        }
        return messageIdentifiers;
    }

    @Override
    public String append(IDescribed factEvent, Stream recipient) throws IllegalArgumentException, MappingException {
        if (factEvent == null) throw new IllegalArgumentException("factEvent parameter is required!");
        String recipientPathName = null;
        String messageId = null;
        if (recipient == null) {
            // Detect potential defined recipient stream name from command
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
            MessageMapper mapper = MessageMapperFactory.getMapper(IDescribed.class, HashMap.class);
            mapper.transform(factEvent);
            Map<String, String> messageBody = (Map<String, String>) mapper.getResult();

            // Send command to identified stream recipient
            connection = client.connect();
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
    public void publish(IDescribed event, Channel recipient) throws IllegalArgumentException, MappingException {
        throw new IllegalArgumentException("ADAPTER IMPL SERVICE TO IMPLEMENT!");
    }

    @Override
    public void publish(IDescribed event, Collection<Channel> recipients) throws IllegalArgumentException, MappingException {
        throw new IllegalArgumentException("ADAPTER IMPL SERVICE TO IMPLEMENT!");
    }

}
