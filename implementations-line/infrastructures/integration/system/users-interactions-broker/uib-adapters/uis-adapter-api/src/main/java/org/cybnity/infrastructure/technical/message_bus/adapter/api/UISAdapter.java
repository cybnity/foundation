package org.cybnity.infrastructure.technical.message_bus.adapter.api;

import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.IDescribed;
import org.cybnity.framework.domain.SerializedResource;
import org.cybnity.framework.immutable.Identifier;

import java.util.Collection;
import java.util.List;

/**
 * Users Interactions Space adapter which expose the API services that allow
 * interactions with the collaboration space.
 *
 * @author olivier
 */
public interface UISAdapter {

    /**
     * For example, disconnect the adapter from the Users Interactions Space.
     */
    void freeUpResources();

    /**
     * Verify the current status of the adapter as healthy and operable for
     * interactions with the Users Interactions Space.
     *
     * @throws UnoperationalStateException When adapter status problem detected.
     */
    void checkHealthyState() throws UnoperationalStateException;

    /**
     * Register observers of streams regarding messages published (e.g domain event, command execution status).
     *
     * @param listeners   Collection of observers. Do nothing when null or empty collection.
     * @param eventMapper Mandatory mapper of event allowing transformation of message type supported by UIS to event types supported by the caller.
     * @throws IllegalArgumentException    When any listener's mandatory content is detected.
     * @throws UnoperationalStateException When system access via adapter is in failure.
     */
    void register(Collection<StreamObserver> listeners, MessageMapper eventMapper) throws IllegalArgumentException, UnoperationalStateException;

    /**
     * Register observers of channels regarding messages published (e.g domain event, command execution status).
     *
     * @param listeners   Collection of observers. Do nothing when null or empty collection.
     * @param eventMapper Mandatory mapper of event allowing transformation of message type supported by UIS to event types supported by the caller.
     * @throws IllegalArgumentException    When any listener's mandatory content is detected.
     * @throws UnoperationalStateException When system access via adapter is in failure.
     */
    void subscribe(Collection<ChannelObserver> listeners, MessageMapper eventMapper) throws IllegalArgumentException, UnoperationalStateException;

    /**
     * Stop listening of messages published to streams that match on or more patterns by observers.
     *
     * @param observers Observers to remove from subscribers of streams.
     */
    void unregister(Collection<StreamObserver> observers);

    /**
     * Stop listening of messages published to channels that match on or more patterns by observers.
     *
     * @param observers Observers to remove from subscribers of channels.
     */
    void unsubscribe(Collection<ChannelObserver> observers);

    /**
     * Get the current active channels of the space.
     *
     * @param namingFilter Optional pattern (e.g specific domain or subdomain context owner of channels to select) regarding active channels to find. When null, all active channels managed by the space are returned.
     * @return A collection of channels or empty list.
     */
    Collection<Channel> activeChannels(String namingFilter);

    /**
     * Add an event to be processed into a space entrypoint with persistence guarantee.
     *
     * @param factEvent   Mandatory fact to execute by a bounded context that is managing a specific entrypoint (e.g as API entrypoint).
     * @param recipient   Optional entrypoint (e.g known domain or subdomain feature name) where the event shall be transmitted.
     * @param eventMapper Mandatory mapper regarding the events supported by the caller that shall be transformed into message types supported by UIS stream.
     * @return Specific technical identifier of the message transmitted into the recipient.
     * @throws IllegalArgumentException    When mandatory parameter is missing. When recipient parameter is not defined and Stream.Specification.STREAM_ENTRYPOINT_PATH_NAME attribute is not detected into the factEvent's specification.
     * @throws MappingException            When factEvent transformation for data structure supported by the recipient is failed.
     * @throws UnoperationalStateException When system access via adapter is in failure.
     */
    String append(IDescribed factEvent, Stream recipient, MessageMapper eventMapper) throws IllegalArgumentException, MappingException, UnoperationalStateException;

    /**
     * Add a fact to be processed into a space entrypoint with persistence guarantee.
     *
     * @param fact        Mandatory fact to execute by a bounded context that is managing a specific stream (e.g as storage flow).
     * @param recipient   Optional recipient of fact where the fact shall be transmitted.
     * @param eventMapper Mandatory mapper regarding the events supported by the caller that shall be transformed into message types supported by UIS stream.
     * @return Specific technical identifier of the message transmitted into the recipient.
     * @throws IllegalArgumentException    When mandatory parameter is missing. When recipient parameter is not defined and Stream.Specification.STREAM_ENTRYPOINT_PATH_NAME attribute is not detected into the factEvent's specification.
     * @throws MappingException            When factEvent transformation for data structure supported by the recipient is failed.
     * @throws UnoperationalStateException When system access via adapter is in failure.
     */
    String append(Object fact, Stream recipient, MessageMapper eventMapper) throws IllegalArgumentException, MappingException, UnoperationalStateException;

    /**
     * Add an event into a space entrypoint with persistence guarantee.
     *
     * @param event       Mandatory fact to add into a stream.
     * @param recipients  Mandatory unique or ordered list of streams where event shall be appended.
     * @param eventMapper Mandatory mapper regarding the events supported by the caller that shall be transformed into message types supported by UIS streams.
     * @return Specific technical identifiers of the messages transmitted into the recipients.
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     * @throws MappingException            When event transformation for data structure supported by the recipients is failed.
     * @throws UnoperationalStateException When system access via adapter is in failure.
     */
    List<String> append(IDescribed event, List<Stream> recipients, MessageMapper eventMapper) throws IllegalArgumentException, MappingException, UnoperationalStateException;

    /**
     * Add a fact into a space entrypoint with persistence guarantee.
     *
     * @param fact        Mandatory fact to add into a stream.
     * @param recipients  Mandatory unique or ordered list of streams where fact shall be appended.
     * @param eventMapper Mandatory mapper regarding the facts supported by the caller that shall be transformed into message types supported by streams.
     * @return Specific technical identifiers of the messages transmitted into the recipients.
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     * @throws MappingException            When event transformation for data structure supported by the recipients is failed.
     * @throws UnoperationalStateException When system access via adapter is in failure.
     */
    List<String> append(Object fact, List<Stream> recipients, MessageMapper eventMapper) throws IllegalArgumentException, MappingException, UnoperationalStateException;

    /**
     * Read all arriving items from one stream identified by its name.
     *
     * @param stream     Mandatory definition of stream to read.
     * @param itemMapper Mandatory mapper regarding the items supported by the caller that shall be transformed for each item type read from stream.
     * @return A list of items existing from stream, or empty list.
     * @throws IllegalArgumentException    When any mandatory parameter is missing.
     * @throws MappingException            When event transformation for data structure supported by the recipient is failed.
     * @throws UnoperationalStateException When system access via adapter is in failure.
     */
    List<Object> readAllFrom(Stream stream, MessageMapper itemMapper) throws IllegalArgumentException, MappingException, UnoperationalStateException;

    /**
     * Read all arriving items from one stream identified by its name.
     *
     * @param stream                Mandatory definition of stream to read.
     * @param itemMapper            Mandatory mapper regarding the items supported by the caller that shall be transformed for each item type read from stream.
     * @param originSubjectIDFilter Optional identifier of origin subject that shall be filtered regarding the items to return.
     * @return A list of items existing from stream, or empty list.
     * @throws IllegalArgumentException    When any mandatory parameter is missing.
     * @throws MappingException            When event transformation for data structure supported by the recipient is failed.
     * @throws UnoperationalStateException When system access via adapter is in failure.
     */
    List<Object> readAllFrom(Stream stream, MessageMapper itemMapper, Identifier originSubjectIDFilter) throws IllegalArgumentException, MappingException, UnoperationalStateException;

    /**
     * Read the items from one stream identified by its name.
     *
     * @param stream                                    Mandatory definition of stream to read.
     * @param afterEventCommittedVersionOfOriginSubject Mandatory identifier of a change event (e.g ID of DomainEvent relative to a modification performed onto an Aggregate) which limit the load of anterior events.
     * @param itemMapper                                Mandatory mapper regarding the items supported by the caller that shall be transformed for each item type read from stream.
     * @param originSubjectIDFilter                     Optional identifier of origin subject that shall be filtered regarding the items to return.
     * @return Found items or empty list.
     * @throws IllegalArgumentException    When any mandatory parameter is missing.
     * @throws MappingException            When event transformation for data structure supported by the recipient is failed.
     * @throws UnoperationalStateException When system access via adapter is in failure.
     */
    List<Object> readAllAfterChangeID(Stream stream, String afterEventCommittedVersionOfOriginSubject, MessageMapper itemMapper, Identifier originSubjectIDFilter) throws IllegalArgumentException, MappingException, UnoperationalStateException;

    /**
     * Find a serialized resource from the space according to its unique logical identifier.
     *
     * @param resourceUniqueIdentifier Mandatory logical identifier (e.g business object UID as resource key) to find.
     * @param resourceNamespaceLabel   Optional namespace of the resource to find.
     * @return A found resource or null.
     * @throws IllegalArgumentException    When any mandatory parameter is missing.
     * @throws UnoperationalStateException When system access via adapter is in failure.
     */
    SerializedResource readSerializedResourceFromID(String resourceUniqueIdentifier, String resourceNamespaceLabel) throws IllegalArgumentException, UnoperationalStateException;

    /**
     * Store a serialized resource identified.
     *
     * @param resource               Mandatory resource to save including mandatory identifier.
     * @param resourceNamespaceLabel Optional namespace of the resource to find.
     * @param expireIn Optional quantity of seconds before the saved resource shall expire.
     * @throws IllegalArgumentException    When any mandatory parameter is missing.
     * @throws UnoperationalStateException When system access via adapter is in failure.
     */
    void saveResource(SerializedResource resource, String resourceNamespaceLabel, Long expireIn) throws IllegalArgumentException, UnoperationalStateException;

    /**
     * Publish an event to be processed into a space entrypoint without persistence and treatment guarantee (e.g if none channel subscriber are active).
     *
     * @param event       Mandatory fact to execute by a bounded context that is responsible.
     * @param recipient   Optional entrypoint (e.g known domain or subdomain name) where the event shall be transmitted.
     * @param eventMapper Mandatory mapper regarding the events supported by the caller that shall be transformed into message types supported by UIS channel.
     * @throws IllegalArgumentException    When mandatory parameter is missing. When recipient parameter is not defined and Channel.Specification.STREAM_ENTRYPOINT_PATH_NAME attribute is not detected into the command's specification.
     * @throws MappingException            When command transformation for data structure supported by the recipient is failed.
     * @throws UnoperationalStateException When system access via adapter is in failure.
     */
    void publish(IDescribed event, Channel recipient, MessageMapper eventMapper) throws IllegalArgumentException, MappingException, UnoperationalStateException;

    /**
     * Notify an event into the space that is need to be promoted to eventual observers.
     *
     * @param event       Mandatory fact to publish over channels.
     * @param recipients  Mandatory unique or multiple channels where event shall be published.
     * @param eventMapper Mandatory mapper regarding the events supported by the caller that shall be transformed into message types supported by UIS channels.
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     * @throws MappingException            When command transformation for data structure supported by the recipient is failed.
     * @throws UnoperationalStateException When system access via adapter is in failure.
     */
    void publish(IDescribed event, Collection<Channel> recipients, MessageMapper eventMapper) throws IllegalArgumentException, MappingException, UnoperationalStateException;

}
