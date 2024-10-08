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
import org.cybnity.framework.UnoperationalStateException;
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.JanusGraphAppSampleAbstractImpl;
import org.janusgraph.core.*;
import org.janusgraph.core.attribute.Geoshape;
import org.janusgraph.core.schema.JanusGraphIndex;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.util.datastructures.IterablesUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of graph sample of objects.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class JanusGraphSchemaCreationUseCaseTest extends ContextualizedJanusGraphActiveTestContainer {

    @Test
    public void createSchema() throws ConfigurationException, IOException, UnoperationalStateException {
        final JanusGraphAppSampleAbstractImpl app = new JanusGraphAppSampleAbstractImpl(this.getContext());
        final GraphTraversalSource g = app.open();
        app.createSchema();
        final JanusGraph janusGraph = (JanusGraph) g.getGraph();
        final JanusGraphManagement management = janusGraph.openManagement();

        final List<String> vertexLabels = IterablesUtil.stream(management.getVertexLabels())
                .map(Namifiable::name).collect(Collectors.toList());
        final List<String> expectedVertexLabels = Stream.of("titan", "location", "god", "demigod", "human", "monster")
                .collect(Collectors.toList());
        assertTrue(vertexLabels.containsAll(expectedVertexLabels));

        final List<String> edgeLabels = IterablesUtil
                .stream(management.getRelationTypes(EdgeLabel.class)).map(Namifiable::name)
                .collect(Collectors.toList());
        final List<String> expectedEdgeLabels = Stream.of("father", "mother", "brother", "pet", "lives", "battled")
                .collect(Collectors.toList());
        assertTrue(edgeLabels.containsAll(expectedEdgeLabels));

        final EdgeLabel father = management.getEdgeLabel("father");
        assertTrue(father.isDirected());
        assertFalse(father.isUnidirected());
        assertEquals(Multiplicity.MANY2ONE, father.multiplicity());

        final List<String> propertyKeys = IterablesUtil
                .stream(management.getRelationTypes(PropertyKey.class)).map(Namifiable::name)
                .collect(Collectors.toList());
        final List<String> expectedPropertyKeys = Stream.of("name", "age", "time", "place", "reason")
                .collect(Collectors.toList());
        assertTrue(propertyKeys.containsAll(expectedPropertyKeys));

        final PropertyKey place = management.getPropertyKey("place");
        assertEquals(Cardinality.SINGLE, place.cardinality());
        assertEquals(Geoshape.class, place.dataType());

        final JanusGraphIndex nameIndex = management.getGraphIndex("nameIndex");
        assertTrue(nameIndex.isCompositeIndex());
        assertEquals(nameIndex.getIndexedElement(), JanusGraphVertex.class);
        final PropertyKey[] nameIndexKeys = nameIndex.getFieldKeys();
        assertEquals(1, nameIndexKeys.length);
        assertEquals("name", nameIndexKeys[0].name());
    }
}
