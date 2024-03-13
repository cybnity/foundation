package org.cybnity.infrastructure.technical.message_bus.adapter.api;

import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.IDescribed;

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
    public void freeUpResources();

    /**
     * Verify the current status of the adapter as healthy and operable for
     * interactions with the Users Interactions Space.
     *
     * @throws UnoperationalStateException When adapter status problem detected.
     */
    public void checkHealthyState() throws UnoperationalStateException;

    /**
     * Register observers of streams regarding messages published (e.g domain event, command execution status).
     *
     * @param listeners   Collection of observers. Do nothing when null or empty collection.
     * @param eventMapper Mandatory mapper of event allowing transformation of message type supported by UIS to event types supported by the caller.
     * @throws IllegalArgumentException When any listener's mandatory content is detected.
     * @throws UnoperationalStateException When system access via adapter is in failure.
     */
    public void register(Collection<StreamObserver> listeners, MessageMapper eventMapper) throws IllegalArgumentException, UnoperationalStateException;

    /**
     * Register observers of channels regarding messages published (e.g domain event, command execution status).
     *
     * @param listeners   Collection of observers. Do nothing when null or empty collection.
     * @param eventMapper Mandatory mapper of event allowing transformation of message type supported by UIS to event types supported by the caller.
     * @throws IllegalArgumentException When any listener's mandatory content is detected.
     * @throws UnoperationalStateException When system access via adapter is in failure.
     */
    public void subscribe(Collection<ChannelObserver> listeners, MessageMapper eventMapper) throws IllegalArgumentException, UnoperationalStateException;

    /**
     * Stop listening of messages published to streams that match on or more patterns by observers.
     *
     * @param observers Observers to remove from subscribers of streams.
     */
    public void unregister(Collection<StreamObserver> observers);

    /**
     * Stop listening of messages published to channels that match on or more patterns by observers.
     *
     * @param observers Observers to remove from subscribers of channels.
     */
    public void unsubscribe(Collection<ChannelObserver> observers);

    /**
     * Get the current active channels of the space.
     *
     * @param namingFilter Optional pattern (e.g specific domain or subdomain context owner of channels to select) regarding active channels to find. When null, all active channels managed by the space are returned.
     * @return A collection of channels or empty list.
     */
    public Collection<Channel> activeChannels(String namingFilter);

    /**
     * Add a factEvent to be processed into a space entrypoint with persistence guarantee.
     *
     * @param factEvent   Mandatory fact to execute by a bounded context that is managing a specific entrypoint (e.g as API entrypoint).
     * @param recipient   Optional entrypoint (e.g known domain or subdomain feature name) where the factEvent shall be transmitted.
     * @param eventMapper Mandatory mapper regarding the events supported by the caller that shall be transformed into message types supported by UIS stream.
     * @return Specific technical identifier of the message transmitted into the recipient.
     * @throws IllegalArgumentException When mandatory parameter is missing. When recipient parameter is not defined and Stream.Specification.STREAM_ENTRYPOINT_PATH_NAME attribute is not detected into the factEvent's specification.
     * @throws MappingException         When factEvent transformation for data structure supported by the recipient is failed.
     * @throws UnoperationalStateException When system access via adapter is in failure.
     */
    public String append(IDescribed factEvent, Stream recipient, MessageMapper eventMapper) throws IllegalArgumentException, MappingException, UnoperationalStateException;

    /**
     * Add an event into a space entrypoint with persistence guarantee.
     *
     * @param event       Mandatory fact to add into a stream.
     * @param recipients  Mandatory unique or ordered list of streams where event shall be appended.
     * @param eventMapper Mandatory mapper regarding the events supported by the caller that shall be transformed into message types supported by UIS streams.
     * @return Specific technical identifiers of the messages transmitted into the recipients.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     * @throws MappingException         When event transformation for data structure supported by the recipients is failed.
     * @throws UnoperationalStateException When system access via adapter is in failure.
     */
    public List<String> append(IDescribed event, List<Stream> recipients, MessageMapper eventMapper) throws IllegalArgumentException, MappingException, UnoperationalStateException;

    /**
     * Publish an event to be processed into a space entrypoint without persistence and treatment guarantee (e.g if none channel subscriber are active).
     *
     * @param event       Mandatory fact to execute by a bounded context that is responsible.
     * @param recipient   Optional entrypoint (e.g known domain or subdomain name) where the event shall be transmitted.
     * @param eventMapper Mandatory mapper regarding the events supported by the caller that shall be transformed into message types supported by UIS channel.
     * @throws IllegalArgumentException When mandatory parameter is missing. When recipient parameter is not defined and Channel.Specification.STREAM_ENTRYPOINT_PATH_NAME attribute is not detected into the command's specification.
     * @throws MappingException         When command transformation for data structure supported by the recipient is failed.
     * @throws UnoperationalStateException When system access via adapter is in failure.
     */
    public void publish(IDescribed event, Channel recipient, MessageMapper eventMapper) throws IllegalArgumentException, MappingException, UnoperationalStateException;

    /**
     * Notify an event into the space that is need to be promoted to eventual observers.
     *
     * @param event       Mandatory fact to publish over channels.
     * @param recipients  Mandatory unique or multiple channels where event shall be published.
     * @param eventMapper Mandatory mapper regarding the events supported by the caller that shall be transformed into message types supported by UIS channels.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     * @throws MappingException         When command transformation for data structure supported by the recipient is failed.
     * @throws UnoperationalStateException When system access via adapter is in failure.
     */
    public void publish(IDescribed event, Collection<Channel> recipients, MessageMapper eventMapper) throws IllegalArgumentException, MappingException, UnoperationalStateException;

}
