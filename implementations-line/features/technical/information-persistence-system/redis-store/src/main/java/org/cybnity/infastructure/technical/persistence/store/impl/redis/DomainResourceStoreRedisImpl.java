package org.cybnity.infastructure.technical.persistence.store.impl.redis;

import io.lettuce.core.StreamMessage;
import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.IDescribed;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.ValueObject;
import org.cybnity.framework.domain.infrastructure.ISnapshotRepository;
import org.cybnity.framework.domain.model.EventStore;
import org.cybnity.framework.domain.model.EventStream;
import org.cybnity.framework.domain.model.IDomainModel;
import org.cybnity.framework.domain.model.ISnapshot;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MappingException;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MessageMapper;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.Stream;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.UISAdapter;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.MessageMapperFactory;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.UISAdapterRedisImpl;

import java.util.LinkedList;
import java.util.List;

/**
 * Redis store of resources.
 *
 * @author olivier
 */
public class DomainResourceStoreRedisImpl extends EventStore {

    /**
     * Adapter to Redis persistence system.
     */
    private final UISAdapter adapter;

    /**
     * Optional activated Snapshots repository.
     */
    private final ISnapshotRepository snapshotsRepository;

    /**
     * Domain which is owner of the persistent object managed by this store.
     */
    private final IDomainModel storeOwner;

    /**
     * Label of the type of persistent object managed by this store.
     */
    private final PersistentObjectNamingConvention.NamingConventionApplicability managedObjectCategoryLabel;

    /**
     * Name space where the snapshots could be stored.
     */
    protected String snapshotsStorageNameSpace;

    /**
     * Current context.
     */
    private final IContext context;

    /**
     * Default constructor.
     *
     * @param ctx                   Mandatory context.
     * @param dataOwner             Mandatory domain which is owner of the persisted object types into the store.
     * @param managedObjectCategory Mandatory type of convention applicable for the type of object which is managed by this store.
     * @param snapshotsCapability   Optional snapshots repository able to be used by this store helping to optimize events rehydration.
     * @throws UnoperationalStateException When impossible instantiation of UISAdapter based on context parameter.
     * @throws IllegalArgumentException    When any mandatory parameter is missing.
     */
    protected DomainResourceStoreRedisImpl(IContext ctx, IDomainModel dataOwner, PersistentObjectNamingConvention.NamingConventionApplicability managedObjectCategory, ISnapshotRepository snapshotsCapability) throws UnoperationalStateException, IllegalArgumentException {
        super();
        if (ctx == null) throw new IllegalArgumentException("Context parameter is required!");
        this.context = ctx;
        this.adapter = new UISAdapterRedisImpl(this.context);
        if (dataOwner == null) throw new IllegalArgumentException("Data owner parameter is required!");
        this.storeOwner = dataOwner;
        if (managedObjectCategory == null)
            throw new IllegalArgumentException("The naming convention parameter is required!");
        this.managedObjectCategoryLabel = managedObjectCategory;
        // Optional snapshots repository for the domain object type
        this.snapshotsRepository = snapshotsCapability;
        if (snapshotsRepository != null)
            // Prepare a namespace usable for snapshots item storage (for example ac:tenant:snapshots)
            snapshotsStorageNameSpace = PersistentObjectNamingConvention.buildNamespace(managedObjectCategoryLabel, storeOwner.domainName(), "snapshots"); // Define store input label (e.g stream dedicated to an object resource)
    }

    /**
     * Get current context.
     * @return A context.
     */
    protected IContext context() {
        return this.context;
    }

    @Override
    public ValueObject<String> snapshotVersionsStorageNamespace() {
        if (snapshotsRepository != null && snapshotsStorageNameSpace != null) {
            // Prepare immutable version of the namespace
            return new SnapshotSpaceName(snapshotsStorageNameSpace);
        }
        return null;
    }

    @Override
    public void freeUpResources() {
        // Freedom of resources allocated by the adapter
        this.adapter.freeUpResources();
    }

    /**
     * Get snapshots providers when existing to optimize the rehydration of store's items.
     *
     * @return Snapshots management provider, or null.
     */
    protected ISnapshotRepository snapshotsRepository() {
        return this.snapshotsRepository;
    }

    /**
     * Get mapper supporting the serialization of domain event type managed as store's items.
     *
     * @return A mapper (IDescribed to StreamMessage).
     */
    protected MessageMapper getDomainEventSerializationMapper() {
        return new MessageMapperFactory().getMapper(IDescribed.class, StreamMessage.class);
    }

    /**
     * Get mapper supporting the deserialization of domain event type managed as store's items.
     *
     * @return A mapper (StreamMessage to IDescribed).
     */
    protected MessageMapper getDomainEventDeserializationMapper() {
        return new MessageMapperFactory().getMapper(StreamMessage.class, IDescribed.class);
    }

    @Override
    public void appendToStream(Identifier domainSubjectId, List<DomainEvent> changes) throws IllegalArgumentException, ImmutabilityException, UnoperationalStateException {
        if (changes == null) throw new IllegalArgumentException("changes parameter is required!");
        if (changes.isEmpty()) return; // noting to change on domain event

        // Define the mapper supporting the event record serialization
        MessageMapper mapper = getDomainEventSerializationMapper();

        // Define the stream resource dedicated to the domain object type
        Stream persistentStream = this.persistentStream(null);

        // --- PREPARE A STREAM ENTRY FOR EACH CHANGE EVENT RELATIVE TO THE SUBJECT IDENTIFIED ---
        for (DomainEvent changeEvt : changes) {
            try {
                // Add the event records to the end of registry stream
                // (regarding all the same event record type version, about the same domain object identifier)
                adapter.append(changeEvt, persistentStream, mapper);
            } catch (MappingException me) {
                // Unsupported type of event
                throw new IllegalArgumentException(me);
            }
        }

        // Promote to subscribers (e.g read-model repositories) the change events that have been stored
        for (DomainEvent changeEvt : changes) {
            subscribersManager().publish(changeEvt);
        }
    }

    /**
     * Get the stream resource used as persistent system by this store.
     *
     * @param domainSubjectId Optional identifier of the dedicated domain object stream identification mean.
     * @return A stream (according its identifier, for example ac:tenant:IYUTFGX754FDFGH).
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    protected Stream persistentStream(String domainSubjectId) throws IllegalArgumentException {
        // Define the stream resource dedicated to the domain object (according its identifier when defined, for example ac:tenant:IYUTFGX754FDFGH)
        // and prepare a label as unique identifier in the store for the event to store
        return new Stream(PersistentObjectNamingConvention.buildComponentName(managedObjectCategoryLabel, storeOwner.domainName(), domainSubjectId)); // Define store input label (e.g common stream storing multiple identifiable aggregates; or dedicated stream to an identified aggregate)
    }

    @Override
    public EventStream loadEventStream(String domainSubjectId) throws IllegalArgumentException, UnoperationalStateException {
        // Define the stream resource dedicated to the domain object type
        Stream persistentStream = this.persistentStream(null);

        LinkedList<DomainEvent> foundEventDomainHistory = new LinkedList<>();
        EventStream domainObjEventsHistory = new EventStream();
        try {
            // Search any stream item equals to event type
            List<Object> foundStreamItems = adapter.readAllFrom(persistentStream,/* mapper supporting the de-serialization*/ getDomainEventDeserializationMapper(), new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), domainSubjectId));
            DomainEvent historizedEvent;
            for (Object streamHistoryItem : foundStreamItems) {
                if (streamHistoryItem != null && DomainEvent.class.isAssignableFrom(streamHistoryItem.getClass())) {
                    historizedEvent = (DomainEvent) streamHistoryItem;
                    // Get the change event relative to the subject
                    foundEventDomainHistory.add(historizedEvent);
                    // Synchronize the event stream version based on the type of record type version hash (aligned with the domain object class serial UID)
                    domainObjEventsHistory.setVersion(historizedEvent.versionHash());
                }
            }
        } catch (MappingException e) {
            throw new UnoperationalStateException(e);
        }
        if (!foundEventDomainHistory.isEmpty()) {
            domainObjEventsHistory.setEvents(foundEventDomainHistory);
            return domainObjEventsHistory;
        }
        return null;
    }

    @Override
    public EventStream loadEventStreamAfterVersion(String domainSubjectId, String snapshotExpectedVersion) throws IllegalArgumentException, UnoperationalStateException {
        if (snapshotExpectedVersion == null || snapshotExpectedVersion.isEmpty())
            throw new IllegalArgumentException("snapshotExpectedVersion parameter is required!");
        LinkedList<DomainEvent> foundEventDomainHistory = new LinkedList<>();
        EventStream domainObjEventsHistory = new EventStream();
        // Search technical identifier of the latest snapshot usable as limit to search about all domain events relative to the snapshot instance
        ISnapshot snapshotItem = (snapshotsRepository() != null) ? snapshotsRepository().getLatestSnapshotById(domainSubjectId, /* storage area into the space*/ snapshotsStorageNameSpace) : /* not supported by snapshots provider */ null;
        if (snapshotItem != null) {
            // Identify the identifier of the origin object last change event, known as commit id
            String lastChangeEventId = snapshotItem.commitVersion();
            if (lastChangeEventId != null && !lastChangeEventId.isEmpty()) {
                // Define the stream resource dedicated to the domain object type
                Stream persistentStream = this.persistentStream(null);
                try {
                    // Search all historized change events after the origin's last change event ID
                    List<Object> foundStreamItems = adapter.readAllAfterChangeID(persistentStream, lastChangeEventId,/* mapper supporting the de-serialization*/ getDomainEventDeserializationMapper(), new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), domainSubjectId));

                    DomainEvent historizedEvent;
                    for (Object streamHistoryItem : foundStreamItems) {
                        // Search latest event stream supporting event type after the snapshot version requested
                        if (streamHistoryItem != null && DomainEvent.class.isAssignableFrom(streamHistoryItem.getClass())) {
                            historizedEvent = (DomainEvent) streamHistoryItem;
                            // Get the change event relative to the subject
                            foundEventDomainHistory.add(historizedEvent);
                            // Synchronize the event stream version based on the type of record type version hash (aligned with the domain object class serial UID)
                            domainObjEventsHistory.setVersion(historizedEvent.versionHash());
                        }
                    }
                } catch (MappingException e) {
                    throw new UnoperationalStateException(e);
                }
            }
        }

        if (!foundEventDomainHistory.isEmpty()) {
            domainObjEventsHistory.setEvents(foundEventDomainHistory);
            return domainObjEventsHistory;
        }
        return null;
    }

    @Override
    public EventStream loadEventStream(String domainSubjectId, int skipEvents, int maxCount) throws IllegalArgumentException, UnoperationalStateException {
        throw new IllegalArgumentException("to implement!");
    }

}
