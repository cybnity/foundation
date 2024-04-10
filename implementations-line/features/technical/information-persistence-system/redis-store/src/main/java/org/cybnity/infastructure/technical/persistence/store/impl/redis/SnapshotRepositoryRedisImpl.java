package org.cybnity.infastructure.technical.persistence.store.impl.redis;

import org.cybnity.framework.IContext;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.ISnapshotRepository;
import org.cybnity.framework.domain.SerializedResource;
import org.cybnity.framework.domain.infrastructure.ResourceDescriptor;
import org.cybnity.framework.domain.model.ISnapshot;
import org.cybnity.framework.domain.model.TenantDescriptor;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.UISAdapter;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.UISAdapterRedisImpl;

import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Date;

/**
 * Snapshot repository implementation class using Redis in-memory resources as embedded snapshots containers.
 */
public class SnapshotRepositoryRedisImpl implements ISnapshotRepository {

    /**
     * Adapter to Redis persistence system.
     */
    private final UISAdapter adapter;

    /**
     * Default constructor.
     *
     * @param ctx Mandatory context.
     * @throws UnoperationalStateException When problem during context usage.
     * @throws IllegalArgumentException    When mandatory parameter is missing.
     */
    public SnapshotRepositoryRedisImpl(IContext ctx) throws UnoperationalStateException, IllegalArgumentException {
        if (ctx == null) throw new IllegalArgumentException("Context parameter is required!");
        this.adapter = new UISAdapterRedisImpl(ctx);
    }

    @Override
    public ISnapshot getLatestSnapshotById(String originObjectIdentifier, String resourceNamespaceName) throws IllegalArgumentException, UnoperationalStateException {
        // Load its instance from store according to the snapshot naming convention
        SerializedResource snapshotContainer = adapter.readSerializedResourceFromID(originObjectIdentifier, resourceNamespaceName);
        if (snapshotContainer != null) {
            // Read origin serialized snapshot instance
            Serializable originSnapshotObject = snapshotContainer.value();
            if (ISnapshot.class.isAssignableFrom(originSnapshotObject.getClass()))
                return (ISnapshot) originSnapshotObject;
        }

        return null;
    }

    @Override
    public void saveSnapshot(ISnapshot snapshot, String resourceNamespaceName) throws IllegalArgumentException, UnoperationalStateException {
        if (snapshot == null) throw new IllegalArgumentException("snapshot parameter is required!");
        try {
            // Prepare container of snapshot resource description
            ResourceDescriptor description = new ResourceDescriptor();
            description.setResourceId(snapshot.versionedObjectUID());// Logical UID of resource
            ObjectStreamClass classType = ObjectStreamClass.lookup(snapshot.getClass());
            if (classType != null) {
                // resource type version uid of stored resource
                description.setResourceTypeSerialVersionUID(Long.valueOf(classType.getSerialVersionUID()).toString());
            }
            description.setVersionDate(snapshot.taken()); // date of snapshot
            description.setAccessibilityNamespace(resourceNamespaceName);

            // Define the snapshot resource to save
            SerializedResource resource = new SerializedResource(snapshot.immutable(), description);

            adapter.saveResource(resource, resourceNamespaceName);
        } catch (ImmutabilityException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
