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

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.framework.domain.event.RandomUUIDFactory;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.infrastructure.impl.projections.SampleDomainGraphImpl;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.domain.service.api.model.SampleDataView;
import org.janusgraph.core.Namifiable;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.util.datastructures.IterablesUtil;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test of graph implementation example about its activation.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ActivateGraphUseCaseTest extends ContextualizedJanusGraphActiveTestContainer {
    protected static SampleDomainGraphImpl graphModel;
    protected static GraphTraversalSource traversal;

    @BeforeAll
    public static void setUpClass() throws ConfigurationException, IOException, UnoperationalStateException {
        graphModel = new SampleDomainGraphImpl(getContextInstance());
        traversal = graphModel.open();
    }

    @BeforeEach
    public void setUp() {
        traversal.V().drop().iterate();
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
        if (graphModel != null) {
            graphModel.close();
        }
        graphModel = null;
    }

    /**
     * Test graph schema creation with success and access to data view over traversal.
     *
     * @throws Exception When problem during graph opening.
     */
    @Test
    public void givenOpenedGraph_whenCreateSchema_thenGraphSpecificationElementsDefined() throws Exception {
        // Create graph schema (graph structure specification)
        graphModel.createSchema();
        // Check that schema was created with success
        JanusGraphManagement graphMgmt = graphModel.janusGraph().openManagement();

        // Verify found vertices normally defined into the graph schema
        String domainNodeType = SampleDataView.class.getSimpleName();
        final List<String> vertexLabels = IterablesUtil.stream(graphMgmt.getVertexLabels())
                .map(Namifiable::name).collect(Collectors.toList());
        final List<String> expectedVertexLabels = Stream.of(/* List of managed domain objects as vertices */domainNodeType)
                .collect(Collectors.toList());
        assertTrue(vertexLabels.containsAll(expectedVertexLabels));

        // Test the creation of a vertex instance
        String sampleLabel = "Stark Industries";
        // Initialize transaction
        Transaction tx = traversal.tx();
        GraphTraversalSource gtx = tx.begin();
        try {
            final Vertex node1 = gtx.addV(/* Vertex nature */domainNodeType)
                    .property(/* Name property */"name", sampleLabel)
                    .property(SampleDataView.PropertyAttributeKey.IDENTIFIED_BY.name(), RandomUUIDFactory.generate(null
                    ))
                    .property(SampleDataView.PropertyAttributeKey.CREATED.name(), Date.from(Instant.now()))
                    .next();
            tx.commit(); // commit creation
            logger.info("SAMPLE SCHEMA INITIALIZED\n" + graphMgmt.printSchema());

            // Test look up vertex by name can use a composite index in JanusGraph
            final Optional<Map<Object, Object>> v = traversal.V().has("name", sampleLabel).valueMap().tryNext();
            if (v.isPresent()) {
                logger.info(v.get().toString());
            } else {
                throw new Exception(sampleLabel + " not found");
            }
        } catch (Exception e) {
            if (graphModel.isSupportsTransactions()) {
                tx.rollback();
            }
            throw e;
        }
    }
}