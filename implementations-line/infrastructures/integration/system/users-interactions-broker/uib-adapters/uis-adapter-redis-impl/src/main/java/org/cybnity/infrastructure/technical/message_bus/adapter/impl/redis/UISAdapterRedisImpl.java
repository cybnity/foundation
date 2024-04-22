package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.api.sync.RedisHashCommands;
import io.lettuce.core.api.sync.RedisStreamCommands;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.IDescribed;
import org.cybnity.framework.domain.SerializedResource;
import org.cybnity.framework.domain.infrastructure.ResourceDescriptor;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.utility.Base64StringConverter;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.*;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.filter.MessageSpecificationEqualsFilter;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.mapper.IDescribedToStreamMessageTransformer;

import java.io.Serializable;
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
 * For implementation documentation of Lettuce library, see <a href="https://lettuce.io/core/release/api/index.html">...</a>
 *
 * @author olivier
 */
public class UISAdapterRedisImpl implements UISAdapter {

    /**
     * Technical logging
     */
    private static final Logger logger = Logger.getLogger(UISAdapterRedisImpl.class.getName());

    /**
     * Temporary key name used as additional hash attribute between a resource store and its read.
     */
    private static final String RESOURCE_VALUE_KEY_NAME = "value";

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
    public UISAdapterRedisImpl(IContext context) throws IllegalArgumentException, UnoperationalStateException {
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
                // Remove singleton instance which is not usable (to force potential singleton re-instantiation in case of getClient() future call)
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
        return this.append((Object) event, recipients, eventMapper);
    }

    @Override
    public List<String> append(Object fact, List<Stream> recipients, MessageMapper factMapper) throws IllegalArgumentException, MappingException, UnoperationalStateException {
        if (fact == null) throw new IllegalArgumentException("Fact parameter is required!");
        if (factMapper == null) throw new IllegalArgumentException("Mapper parameter is required!");
        if (recipients == null) throw new IllegalArgumentException("Recipients parameter is required!");
        if (recipients.isEmpty())
            throw new IllegalArgumentException("Recipients parameter shall include minimum one recipient!");
        List<String> messageIdentifiers = new LinkedList<>();
        // Append the fact on each recipient
        for (Stream s : recipients) {
            messageIdentifiers.add(this.append(fact, s, factMapper));
        }
        return messageIdentifiers;
    }

    @Override
    public String append(Object fact, Stream recipient, MessageMapper mapper) throws IllegalArgumentException, MappingException, UnoperationalStateException {
        if (fact == null) throw new IllegalArgumentException("Fact parameter is required!");
        if (mapper == null) throw new IllegalArgumentException("Mapper parameter is required!");
        String recipientPathName = null;
        String messageId = null;
        if (recipient == null && IDescribed.class.isAssignableFrom(fact.getClass())) {
            // Detect potential defined recipient stream name from event
            IDescribed description = (IDescribed) fact;
            for (Attribute specification : description.specification()) {
                if (Stream.Specification.STREAM_ENTRYPOINT_PATH_NAME.name().equals(specification.name())) {
                    recipientPathName = specification.value();
                    break; // Stop search
                }
            }
        } else if (recipient != null) {
            recipientPathName = recipient.name();
        }
        if (recipientPathName == null || recipientPathName.isEmpty())
            throw new IllegalArgumentException("Recipient stream name not defined. Impossible push of factEvent on the space!");
        StatefulRedisConnection<String, String> connection = null;
        try {
            // Transform event into supported message type
            mapper.transform(fact);
            Map<String, String> messageBody = (Map<String, String>) mapper.getResult();

            // Send event to identified stream recipient
            connection = getClient().connect();
            RedisCommands<String, String> syncCommands = connection.sync();
            messageId = syncCommands.xadd(/* recipient name to feed */ recipientPathName, /* fact record transformed */ messageBody);
        } catch (ClassCastException cce) {
            // Transformation result cast problem
            throw new MappingException(cce);
        } finally {
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        }
        return messageId;
    }

    @Override
    public String append(IDescribed event, Stream recipient, MessageMapper eventMapper) throws IllegalArgumentException, MappingException, UnoperationalStateException {
        return this.append((Object) event, recipient, eventMapper);
    }

    @Override
    public List<Object> readAllFrom(Stream stream, MessageMapper itemMapper, Identifier originSubjectIDFilter) throws IllegalArgumentException, MappingException, UnoperationalStateException {
        if (stream == null) throw new IllegalArgumentException("stream parameter is required!");
        if (itemMapper == null) throw new IllegalArgumentException("itemMapper parameter is required!");
        List<Object> foundEntries = new LinkedList<>(); // empty list returned by default
        StatefulRedisConnection<String, String> connection = null;
        try {
            connection = getClient().connect();
            RedisCommands<String, String> sync = connection.sync();
            // Execute synchronous read command (read of all stream's items)
            List<StreamMessage<String, String>> streamItems = sync.xread(XReadArgs.StreamOffset.from(/* stream path name to read */ stream.name(), /* All history of foundEntries having an ID greater than 0-0 */ StreamObserver.DEFAULT_OBSERVATION_PATTERN));
            Object retrievedItem;

            // Apply filtering conditions to select only the items about equals origin subject id
            MessageSpecificationEqualsFilter filter = new MessageSpecificationEqualsFilter();
            Map<String, String> selectionCriteria = new HashMap<>();
            selectionCriteria.put(MessageSpecificationEqualsFilter.FilteringCriteria.ORIGIN_SUBJECT_ID_PARAM_NAME.paramName(), (originSubjectIDFilter != null) ? originSubjectIDFilter.value().toString() : null);
            // Execute the results reduction
            List<StreamMessage<String, String>> whereEqualOriginSubjectItems = filter.apply(streamItems, selectionCriteria, null);

            // Get the filtered results to return
            for (StreamMessage<String, String> equalsOriginSubjectItem : whereEqualOriginSubjectItems) {
                if (equalsOriginSubjectItem != null) {
                    try {
                        // Try to transform event into supported object type to return
                        itemMapper.transform(equalsOriginSubjectItem);
                        retrievedItem = itemMapper.getResult();
                        if (retrievedItem != null) {
                            foundEntries.add(retrievedItem);
                        }
                    } catch (MappingException mpe) {
                        // Stream entry (e.g snapshot version) that is not into filtered structure (e.g change event)
                        // For example, entity snapshot record that shall not be retrieved as searched domain events
                        // So ignore filtered equalsOriginSubjectItem
                    }
                }
            }
            return foundEntries;
        } finally {
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        }
    }

    @Override
    public List<Object> readAllFrom(Stream stream, MessageMapper itemMapper) throws IllegalArgumentException, MappingException, UnoperationalStateException {
        return this.readAllFrom(stream, itemMapper, null);
    }

    /**
     * Get the type of codec usable for encoding of a key value.
     *
     * @return An UTF8 string codec.
     */
    private RedisCodec<String, String> resourceKeyCodec() {
        return StringCodec.UTF8;
    }

    /**
     * Prepare a full path to storage area of a resource.
     * @param resourceNamespaceLabel Optional label designing a resource path.
     * @return A full path name.
     */
    private StringBuilder buildResourceFullPath(String resourceNamespaceLabel)  {
        StringBuilder resourceKeyName = new StringBuilder();
        if (resourceNamespaceLabel != null && !resourceNamespaceLabel.isEmpty()) {
            // Add namespace prefix
            resourceKeyName.append(resourceNamespaceLabel);
            // Add separator
            resourceKeyName.append(NamingConventions.KEY_NAME_SEPARATOR);
        }
        return resourceKeyName;
    }

    @Override
    public SerializedResource readSerializedResourceFromID(String resourceUniqueIdentifier, String resourceNamespaceLabel) throws IllegalArgumentException, UnoperationalStateException {
        if (resourceUniqueIdentifier == null || resourceUniqueIdentifier.isEmpty())
            throw new IllegalArgumentException("Resource unique identifier parameter is required!");
        StatefulRedisConnection<String, String> connection = null;
        try {
            // See https://redis.io/docs/latest/develop/data-types/hashes/ for help

            // Build resource key name where to save the resource
            StringBuilder resourceKeyName = new StringBuilder();
            if (resourceNamespaceLabel != null && !resourceNamespaceLabel.isEmpty()) {
                resourceKeyName.append(buildResourceFullPath(resourceNamespaceLabel));
            }
            // Add resource UID
            resourceKeyName.append(resourceUniqueIdentifier);

            connection = getClient().connect(resourceKeyCodec());
            RedisHashCommands<String, String> sync = connection.sync();

            // Read record type structured as collection of field-value pairs
            Map<String, String> record = sync.hgetall(resourceKeyName.toString());
            String originResourceBase64StringConverted = record.get(RESOURCE_VALUE_KEY_NAME); // get origin serialized object previously stored as string
            if (originResourceBase64StringConverted != null && !originResourceBase64StringConverted.isEmpty()) {
                // Rebind the original object
                Optional<Serializable> convertedOriginResource = Base64StringConverter.convertFrom(originResourceBase64StringConverted);
                if (convertedOriginResource.isPresent()) {
                    Serializable originObject = convertedOriginResource.get();
                    ResourceDescriptor desc = new ResourceDescriptor(record);
                    // Remove the temporary added RESOURCE_VALUE_KEY_NAME attribute during the persistence in store
                    desc.remove(RESOURCE_VALUE_KEY_NAME);
                    // Return found resource object
                    return new SerializedResource(originObject, /* Retrieve the resource description */ desc);
                }
            }

            return null;
        } finally {
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        }
    }

    @Override
    public void saveResource(SerializedResource resource, String resourceNamespaceLabel) throws IllegalArgumentException, UnoperationalStateException {
        if (resource == null) throw new IllegalArgumentException("Resource parameter is required!");
        if (resource.description() == null) throw new IllegalArgumentException("Resource description is required!");
        if (resource.description().resourceId() == null || resource.description().resourceId().isEmpty())
            throw new IllegalArgumentException("Resource unique identifier parameter is required!");

        StatefulRedisConnection<String, String> connection = null;
        try {
            // See https://redis.io/docs/latest/develop/data-types/hashes/ for help

            // Create String serialized version of the origin resource value
            Serializable value = resource.value();
            Optional<String> stringConverted = (value != null) ? Base64StringConverter.convertToString(value) : Optional.empty();

            Long count = null;
            if (stringConverted != null && stringConverted.isPresent() && !stringConverted.get().isEmpty()) {
                connection = getClient().connect(resourceKeyCodec());
                RedisHashCommands<String, String> sync = connection.sync();

                // Build resource key name where to save the resource
                StringBuilder resourceKeyName = new StringBuilder();
                if (resourceNamespaceLabel != null && !resourceNamespaceLabel.isEmpty()) {
                    resourceKeyName.append(buildResourceFullPath(resourceNamespaceLabel));
                }
                // Add resource UID
                resourceKeyName.append(resource.description().resourceId());

                // Build hashmap item as Redis queryable record to be stored, included denormalized values of the resource description and resource value
                Map<String, String> record = new HashMap<>(resource.description()); // Read all description values
                record.put(RESOURCE_VALUE_KEY_NAME, stringConverted.get()); // Read origin serialized object value as converted String

                // Use record types structured as collections of field-value pairs
                count = sync.hset(/* resource key */ resourceKeyName.toString(), /* serialized resource */ record);
            }
            if (count != null && count.intValue() >= 1) return;
            throw new UnoperationalStateException("Impossible serialization of the resource to store!");
        } finally {
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        }
    }

    @Override
    public List<Object> readAllAfterChangeID(Stream stream, String afterEventCommittedVersionOfOriginSubject, MessageMapper itemMapper, Identifier originSubjectIDFilter) throws IllegalArgumentException, MappingException, UnoperationalStateException {
        if (stream == null) throw new IllegalArgumentException("stream parameter is required!");
        if (itemMapper == null) throw new IllegalArgumentException("itemMapper parameter is required!");
        if (afterEventCommittedVersionOfOriginSubject == null || afterEventCommittedVersionOfOriginSubject.isEmpty())
            throw new IllegalArgumentException("afterEventCommittedVersionOfOriginSubject parameter is required!");
        List<Object> foundEntries = new LinkedList<>(); // empty list returned by default

        // Identify the technical identifier of the last change event from the stream
        String limitationChangeEventTechnicalId = this.findFactTechnicalID(stream, afterEventCommittedVersionOfOriginSubject);
        StatefulRedisConnection<String, String> connection = null;
        try {
            connection = getClient().connect();
            if (limitationChangeEventTechnicalId != null) {
                RedisStreamCommands<String, String> sync = connection.sync();
                // Execute synchronous read command (read all items from a stream within a specific Range in reverse order)
                Range<String> range = Range.create(/* lower id */ limitationChangeEventTechnicalId, /* upper */ "+");
                List<StreamMessage<String, String>> streamItems = sync.xrange(/* stream path name to read */ stream.name(), /* Last found entry ID -1 as the last element of the list */ range);

                // Apply filtering conditions to select only the items about equals origin subject id
                MessageSpecificationEqualsFilter filter = new MessageSpecificationEqualsFilter();
                Map<String, String> selectionCriteria = new HashMap<>();
                selectionCriteria.put(MessageSpecificationEqualsFilter.FilteringCriteria.ORIGIN_SUBJECT_ID_PARAM_NAME.paramName(), (originSubjectIDFilter != null) ? originSubjectIDFilter.value().toString() : null);
                // Execute the results reduction
                List<StreamMessage<String, String>> whereEqualOriginSubjectItems = filter.apply(streamItems, selectionCriteria, null);

                Object retrievedItem;
                for (StreamMessage<String, String> item : whereEqualOriginSubjectItems) {
                    if (item != null) {
                        try {
                            // Try to transform event into supported object type to return
                            itemMapper.transform(item);
                            retrievedItem = itemMapper.getResult();
                            if (retrievedItem != null) {
                                foundEntries.add(retrievedItem);
                            }
                        } catch (MappingException mpe) {
                            // Stream entry (e.g snapshot version) that is not into filtered structure (e.g change event)
                            // For example, entity snapshot record that shall not be retrieved as searched domain events
                            // So ignore filtered item
                        }
                    }
                }
            }
            return foundEntries;
        } finally {
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        }
    }

    private String findFactTechnicalID(Stream stream, String afterEventCommittedVersionOfOriginSubject) throws IllegalArgumentException, UnoperationalStateException {
        if (stream == null) throw new IllegalArgumentException("stream parameter is required!");
        if (afterEventCommittedVersionOfOriginSubject == null || afterEventCommittedVersionOfOriginSubject.isEmpty())
            throw new IllegalArgumentException("afterEventCommittedVersionOfOriginSubject parameter is required!");
        StatefulRedisConnection<String, String> connection = null;
        try {
            connection = getClient().connect();
            RedisStreamCommands<String, String> sync = connection.sync();
            // Execute synchronous read command (read all items from a stream within a specific Range in reverse order)
            Range<String> range = Range.create(/* lower */ "-", /* upper */ "+");
            List<StreamMessage<String, String>> streamItems = sync.xrevrange(/* stream path name to read */ stream.name(), /* Last found entry ID -1 as the last element of the list */ range);

            // From latest items stored in stream, analyze found items to detect the limit domain event
            Map<String, String> itemBody;
            String factRecordAttributeName = IDescribedToStreamMessageTransformer.queryAttributeAboutFactRecordID(); // Attribute in message regarding fact record id (e.g change event id)
            for (StreamMessage<String, String> item : streamItems) {
                if (item != null) {
                    itemBody = item.getBody();
                    String factRecordID = itemBody.get(factRecordAttributeName); // Detect existing fact record value in message
                    if (factRecordID != null) {
                        // Evaluate filtering criteria to determine if item shall be retained because equals to the commit version
                        if (afterEventCommittedVersionOfOriginSubject.equals(factRecordID)) {
                            // This persisted message is the limit of event recorded regarding the origin subject
                            // Read its technical identifier (automatically generated by the Redis stream)
                            return item.getId();
                        }
                    }
                }
            }
        } finally {
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        }
        return null; // None found
    }

    @Override
    public void publish(IDescribed event, Channel recipient, MessageMapper eventMapper) throws IllegalArgumentException, MappingException, UnoperationalStateException {
        if (event == null) throw new IllegalArgumentException("Event parameter is required!");
        if (eventMapper == null) throw new IllegalArgumentException("Event mapper parameter is required!");
        String recipientPathName = null;
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
