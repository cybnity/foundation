// Copyright 2017 JanusGraph Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph;

import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.*;
import org.cybnity.framework.domain.event.ConcreteDomainChangeEvent;
import org.cybnity.framework.domain.event.IEventType;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.domain.model.IDomainEventSubscriber;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.event.SampleDomainEventType;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.event.SampleDomainQueryEventType;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.SampleDomainTransactionsRepository;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.model.SampleDataView;
import org.junit.jupiter.api.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;


/**
 * Test of implemented repository relative to a perimeter of projections.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class DomainTransactionsRepositoryUseCaseTest extends ContextualizedJanusGraphActiveTestContainer {

    private static SampleDomainTransactionsRepository repo;

    @BeforeEach
    public void initRepository() throws UnoperationalStateException {
        // Prepare a repository of a sample domain managing a read-model projections perimeter
        repo = SampleDomainTransactionsRepository.instance(getContext());
    }

    @AfterEach
    public void cleanResources() throws UnoperationalStateException {
        repo.graphModel().drop();//delete previous created schema and records
        repo.freeResources();
        repo = null;
    }

    /**
     * Test that propagation of a write model changed confirmation (e.g domain event relative to an aggregate) is handled by repository,
     * which notify the read-model projections potentially managing a scope of data-view versions (read-model of data view versions relative to the aggregate modified in write-model) and verify if data view version (e.g graph Vertex) is automatically created.
     * Simulate in second step a changed version of the initial aggregate, and verify that its data-view in read-model is automatically upgraded (refreshed view).
     *
     * @throws Exception When problem during test execution.
     */
    @Test
    public void givenChangedDomainObject_whenNotifyWriteModelChangeToRepository_thenReadModelDataViewCreated() throws Exception {
        // --- Prepare subscriber allowing control of normally promoted data-view projection change events regarding the manipulated read-model
        List<String> toDetect = new LinkedList<>();
        // Declare interest for sample data-view versions
        toDetect.add(SampleDomainEventType.SAMPLE_DATAVIEW_CREATED.name());
        toDetect.add(SampleDomainEventType.SAMPLE_DATAVIEW_REFRESHED.name());
        EventsCheck checker = new EventsCheck(toDetect);
        repo.subscribe(checker); // Register subscriber

        // Simulate a confirmation domain event relative to a changed domain aggregate (e.g by event store)
        Identifier originAggregateId = IdentifierStringBased.generate(null);
        Identifier creationSourcePredecessorReferenceId = IdentifierStringBased.generate(null);
        OffsetDateTime createdAt = OffsetDateTime.now();

        // Simulate a set of values equals to the original aggregate (normally read by each projection to extract the current value from aggregate, but here values integrated to the event for simple read by SampleDataViewStateTransactionImpl test class)
        final Collection<Attribute> changeSpecification = new HashSet<>();
        changeSpecification.add(new Attribute(SampleDataView.PropertyAttributeKey.IDENTIFIED_BY.name(), originAggregateId.value().toString())); // aggregate identifier value
        String aggregateLabel = "Stark Industries";
        changeSpecification.add(new Attribute(SampleDataView.PropertyAttributeKey.NAME.name(), aggregateLabel)); // Label of the aggregate (e.g like a tenant name which shall be unique in graph model)
        DateFormat formatter = new SimpleDateFormat(SerializationFormat.DATE_FORMAT_PATTERN);
        changeSpecification.add(new Attribute(SampleDataView.PropertyAttributeKey.CREATED.name(), formatter.format(Date.from(createdAt.toInstant()))));
        String commitVersion = "ab1"; // Simulate an unique commit version of change fact
        changeSpecification.add(new Attribute(SampleDataView.PropertyAttributeKey.COMMIT_VERSION.name(), commitVersion));
        changeSpecification.add(new Attribute(SampleDataView.PropertyAttributeKey.LAST_UPDATED_AT.name(), formatter.format(Date.from(createdAt.toInstant()))));

        // Simulate notification from write-model store (e.g event store subscribed by read-model repository) to the repository normally observer of the write-model changes
        CompletableFuture<Boolean> committed = CompletableFuture.supplyAsync(() -> {
            repo.handleEvent(createChangeEvent(/* type of origin object creation */ SampleDomainEventType.SAMPLE_AGGREGATE_CREATED, creationSourcePredecessorReferenceId, originAggregateId, createdAt, changeSpecification));
            return Boolean.TRUE;
        });

        if (committed.get()) {
            // Execute query based on label filtering
            Map<String, String> queryParameters = prepareQueryBasedOnLabel(aggregateLabel, SampleDataView.class.getSimpleName(), SampleDomainQueryEventType.SAMPLE_DATAVIEW_FIND_BY_LABEL);
            List<SampleDataView> results = repo.queryWhere(queryParameters, sessionCtx);

            // Verify if a first version of the data view (projection view relative to the aggregate) have been created into the graph model
            Assertions.assertNotNull(results, "Existing data view should have been found!");
            Assertions.assertFalse(results.isEmpty(), "First created data view should have been found!");

            // Check some data view attributes that should be equals to the original aggregate version
            SampleDataView recordedDataViewState = results.get(0);
            Assertions.assertEquals(originAggregateId.value().toString(), recordedDataViewState.valueOfProperty(SampleDataView.PropertyAttributeKey.IDENTIFIED_BY), "Identifier of data view shall be the id of the original aggregate!");
            Assertions.assertEquals(aggregateLabel, recordedDataViewState.valueOfProperty(SampleDataView.PropertyAttributeKey.NAME), "Invalid data view label!");
            Assertions.assertEquals(commitVersion, recordedDataViewState.valueOfProperty(SampleDataView.PropertyAttributeKey.COMMIT_VERSION), "Invalid data view commit version!");
        }
        // Simulate a confirmation of modified aggregate
        Identifier changeSourcePredecessorReferenceId = IdentifierStringBased.generate(null);
        OffsetDateTime modifiedAt = OffsetDateTime.now();
        // Simulate a set of values equals to the original aggregate
        changeSpecification.clear();
        changeSpecification.add(new Attribute(SampleDataView.PropertyAttributeKey.IDENTIFIED_BY.name(), originAggregateId.value().toString())); // aggregate identifier value
        changeSpecification.add(new Attribute(SampleDataView.PropertyAttributeKey.NAME.name(), aggregateLabel)); // Same vertex label (node) without change to avoid creation of another node based on name
        changeSpecification.add(new Attribute(SampleDataView.PropertyAttributeKey.CREATED.name(), formatter.format(Date.from(createdAt.toInstant())))); // none change of original value of aggregate creation date
        commitVersion = "ct2"; // Simulate changed commit version during the aggregate modification in write-model
        changeSpecification.add(new Attribute(SampleDataView.PropertyAttributeKey.COMMIT_VERSION.name(), commitVersion));
        changeSpecification.add(new Attribute(SampleDataView.PropertyAttributeKey.LAST_UPDATED_AT.name(), formatter.format(Date.from(modifiedAt.toInstant())))); // version modified at

        // Simulate notification from write-model store (e.g event store subscribed by read-model repository) to the repository normally observer of the write-model changes
        repo.handleEvent(createChangeEvent(/* type of origin object change */ SampleDomainEventType.SAMPLE_AGGREGATE_CHANGED, changeSourcePredecessorReferenceId, originAggregateId, modifiedAt, changeSpecification));

        // Execute query based on label filtering to find new updated data-view
        Map<String, String> queryParameters = prepareQueryBasedOnLabel(aggregateLabel, SampleDataView.class.getSimpleName(), SampleDomainQueryEventType.SAMPLE_DATAVIEW_FIND_BY_LABEL);
        List<SampleDataView> results = repo.queryWhere(queryParameters, sessionCtx);

        // Verify if a only one version of the data view type (projection view relative to the aggregate) have been retrieved from the graph model
        Assertions.assertEquals(1, results.size(), "Only unique existing data view should have been found!");

        // Verify if existing data view status had been modified with new attribute (e.g commit version value)
        SampleDataView recordedDataViewState = results.get(0);
        Assertions.assertEquals(commitVersion, recordedDataViewState.valueOfProperty(SampleDataView.PropertyAttributeKey.COMMIT_VERSION), "Invalid data view commit version that shall have been modified during refresh!");
        Assertions.assertEquals(originAggregateId.value().toString(), recordedDataViewState.valueOfProperty(SampleDataView.PropertyAttributeKey.IDENTIFIED_BY), "Identifier of data view shall be the same than existing aggregate previous vertex!");

        // --- CHANGE NOTIFICATION TO READ-MODEL VERIFICATION ---
        // Verify that each data view change performed on the read-model by the repository, have been notified via change events that were handled by checker
        Assertions.assertTrue(checker.isAllEventsToCheckHaveBeenFound(), checker.notAlreadyChecked.size() + " data view changes had not been notified to subscriber!");
    }

    /**
     * Prepare simple query based on a vertex label and type.
     *
     * @param label          Mandatory.
     * @param domainNodeType Mandatory.
     * @return A parameter set.
     */
    private Map<String, String> prepareQueryBasedOnLabel(String label, String domainNodeType, IEventType queryType) {
        Map<String, String> queryParameters = new HashMap<>();
        // Explicit query name to perform
        queryParameters.put(Command.TYPE, queryType.name());
        // Query filtering criteria definition
        queryParameters.put(SampleDataView.PropertyAttributeKey.NAME.name(), label); // Search vertex (data-view) node with equals name
        queryParameters.put(SampleDataView.PropertyAttributeKey.DATAVIEW_TYPE.name(), domainNodeType); // type of vertex (node type in graph model)
        return queryParameters;
    }

    /**
     * Prepare a domain event simulating a write-model object changed.
     *
     * @param type                               Mandatory.
     * @param changeSourcePredecessorReferenceId Mandatory.
     * @param originObjectId                     Mandatory.
     * @param changedAt                          Mandatory.
     * @param changeSpecification                Optional.
     * @return Example of simulated event.
     */
    private DomainEvent createChangeEvent(Enum<?> type, Identifier changeSourcePredecessorReferenceId, Identifier originObjectId, OffsetDateTime changedAt, Collection<Attribute> changeSpecification) {
        ConcreteDomainChangeEvent changeEvt = new ConcreteDomainChangeEvent( /* new technical identifier of the change event fact */
                new DomainEntity(IdentifierStringBased.generate(null))
                , /* Type of change committed */ type);
        // Add mandatory description regarding the fact basic definition attributes
        changeEvt.setChangeSourcePredecessorReferenceId(changeSourcePredecessorReferenceId);
        changeEvt.setChangeSourceIdentifier(originObjectId); // Origin domain model object changed
        changeEvt.setChangeSourceOccurredAt(changedAt);
        if (changeSpecification != null) {
            // Add aggregate last value that normally shall not be required by repository into the received event because AbstractRealModelDataViewProjection shall be capable to request the origin aggregate to extract the values which make sens for preparation of its data-view read model version
            for (Attribute at : changeSpecification) {
                changeEvt.appendSpecification(at);
            }
        }
        return changeEvt;
    }

    /**
     * Utility class ensuring handling of data-view changes notified by any projection.
     */
    private static class EventsCheck implements IDomainEventSubscriber<DomainEvent> {

        /**
         * List of event type names to detect
         */
        private final List<String> notAlreadyChecked = new LinkedList<>();

        public EventsCheck(List<String> toCheck) {
            super();
            // Prepare validation container
            notAlreadyChecked.addAll(toCheck);
        }

        @Override
        public void handleEvent(DomainEvent event) {
            if (event != null) {
                // Search and remove any existing from the list of origins to check
                notAlreadyChecked.remove(event.type().value());
            }
        }

        @Override
        public Class<?> subscribeToEventType() {
            return DomainEvent.class;
        }

        public boolean isAllEventsToCheckHaveBeenFound() {
            return this.notAlreadyChecked.isEmpty();
        }
    }
}