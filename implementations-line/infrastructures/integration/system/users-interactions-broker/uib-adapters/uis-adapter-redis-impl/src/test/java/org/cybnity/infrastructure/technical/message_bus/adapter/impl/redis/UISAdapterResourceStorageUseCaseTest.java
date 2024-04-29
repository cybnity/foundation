package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.SerializedResource;
import org.cybnity.framework.domain.infrastructure.ResourceDescriptor;
import org.cybnity.framework.domain.model.ConcreteSnapshot;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.domain.model.Tenant;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.UISAdapter;
import org.junit.jupiter.api.*;

import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Test and check the usage of Redis store via Lettuce adapter for resources management (e.g serialized object save and read).
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UISAdapterResourceStorageUseCaseTest extends ContextualizedRedisActiveTestContainer {

    private final Logger logger = Logger.getLogger(UISAdapterResourceStorageUseCaseTest.class.getName());

    private UISAdapter adapter;

    private Tenant tenant;
    private ConcreteSnapshot snapshot;
    private Long expirationTime;

    @BeforeEach
    public void definedSamples() throws Exception {
        this.tenant = new Tenant(/* predecessor*/ new DomainEntity(IdentifierStringBased.generate(null)),/* tenant id */IdentifierStringBased.generate(null), "CYBNITY");
        this.snapshot = new ConcreteSnapshot(tenant);
        Assertions.assertNotNull(snapshot.commitVersion());
        adapter = new UISAdapterRedisImpl(getContext());
        expirationTime = 80L;
    }

    @AfterEach
    public void removeResources() {
        if (adapter != null)
            adapter.freeUpResources();
        adapter = null;
        this.snapshot = null;
        this.tenant = null;
        expirationTime = null;
    }

    /**
     * Build and return a example of resource.
     *
     * @param resourceNamespaceName Optional namespace where resource is accessible.
     * @return A resource.
     * @throws ImmutabilityException When problem of instance preparation.
     */
    private SerializedResource prepareResourceSample(String resourceNamespaceName) throws ImmutabilityException {
        // Prepare a description of serializable snapshot of tenant simulation
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
        return new SerializedResource(snapshot.immutable(), description);
    }

    /**
     * Test that a valid serialized resource based on an Aggregate instance, is stored in Redis and is able to retrieve since its logical identifier.
     *
     * @throws Exception Sample preparation problem.
     */
    @Test
    public void givenValidSerializedResource_whenSaveAttempt_thenSuccessRedisStorage() throws Exception {
        // Prepare serialized resource including description
        // Define store input label (e.g stream dedicated to an object resource)
        String namespace = "ac:tenant:snapshots";
        SerializedResource originResource = prepareResourceSample(namespace);

        // Identify the logical ID of origin aggregate instance
        String originObjectIdentifier = originResource.description().resourceId();

        // Save in Redis store as persistent allowing future retrieving
        adapter.saveResource(originResource, namespace, expirationTime);

        // Attempt to re-load snapshot from store
        SerializedResource reloadedResource = adapter.readSerializedResourceFromID(originObjectIdentifier, namespace);
        Assertions.assertNotNull(reloadedResource, "stored record shall have been found!");

        // Verify reloaded contents equals to origin instance(s)
        Serializable reloadedObj = reloadedResource.value();
        Assertions.assertTrue(ConcreteSnapshot.class.isAssignableFrom(reloadedObj.getClass()), "Invalid type of reloaded origin snapshot instance type!");
        ConcreteSnapshot snapshotReloaded = (ConcreteSnapshot) reloadedObj;

        ResourceDescriptor reloadedDescription = reloadedResource.description();
        isEquals(originResource.description(), reloadedDescription); // Equals description?
    }

    private void isEquals(ResourceDescriptor expected, ResourceDescriptor other) {
        // Check qty of description attributes
        Assertions.assertEquals(expected.size(), other.size(), "Invalid qty of defined description attributes!");
        Assertions.assertEquals(expected.resourceId(), other.resourceId());
        Assertions.assertEquals(expected.accessibilityNamespace(), other.accessibilityNamespace());
        Assertions.assertEquals(expected.resourceTypeSerialVersionUID(), other.resourceTypeSerialVersionUID());
        Assertions.assertEquals(expected.versionDate(), other.versionDate());
        Assertions.assertEquals(expected.versionHash(), other.versionHash());
    }
}