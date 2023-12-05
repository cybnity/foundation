package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.*;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Current context of adapter runtime.
     */
    private IContext context;

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
    public void register(Collection<StreamObserver> observers) {
        throw new IllegalArgumentException("ADAPTER IMPL SERVICE TO IMPLEMENT!");
    }

    @Override
    public void subscribe(Collection<ChannelObserver> observers) {
        throw new IllegalArgumentException("ADAPTER IMPL SERVICE TO IMPLEMENT!");
    }

    @Override
    public void unregister(Collection<StreamObserver> observers) {
        throw new IllegalArgumentException("ADAPTER IMPL SERVICE TO IMPLEMENT!");
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
    public String append(DomainEvent event, List<Stream> recipients) throws IllegalArgumentException, MappingException {
        throw new IllegalArgumentException("ADAPTER IMPL SERVICE TO IMPLEMENT!");
    }

    @Override
    public String append(Command command, Stream recipient) throws IllegalArgumentException, MappingException {
        if (command == null) throw new IllegalArgumentException("command parameter is required!");
        String recipientPathName = null;
        String messageId = null;
        if (recipient == null) {
            // Detect potential defined recipient stream name from command
            for (Attribute specification : command.specification()) {
                if (Stream.Specification.STREAM_ENTRYPOINT_PATH_NAME.name().equals(specification.name())) {
                    recipientPathName = specification.value();
                    break; // Stop search
                }
            }
        } else {
            recipientPathName = recipient.name();
        }
        if (recipientPathName == null || recipientPathName.isEmpty())
            throw new IllegalArgumentException("Recipient stream name not defined. Impossible push of command on the space!");
        StatefulRedisConnection<String, String> connection = null;
        try {
            // Transform event into supported message type
            MessageMapper mapper = MessageMapperFactory.getMapper(Command.class, HashMap.class);

            mapper.transform(command);
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
    public void publish(Command command, Channel recipient) throws IllegalArgumentException, MappingException {
        throw new IllegalArgumentException("ADAPTER IMPL SERVICE TO IMPLEMENT!");
    }

    @Override
    public void publish(DomainEvent event, Collection<Channel> recipients) throws IllegalArgumentException, MappingException {
        throw new IllegalArgumentException("ADAPTER IMPL SERVICE TO IMPLEMENT!");
    }

}
