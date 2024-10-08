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
import org.cybnity.infrastructure.technical.registry.repository.impl.janusgraph.sample.GraphAppSampleAbstractImpl;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Test of graph sample of objects.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class GraphElementsManagementUseCaseTest extends ContextualizedJanusGraphActiveTestContainer {
    protected static GraphAppSampleAbstractImpl app;
    protected static GraphTraversalSource g;

    @BeforeAll
    public static void setUpClass() throws ConfigurationException, IOException, UnoperationalStateException {
        app = new GraphAppSampleAbstractImpl(getContextInstance());
        g = app.open();
    }

    @BeforeEach
    public void setUp() {
        g.V().drop().iterate();
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
        if (app != null) {
            app.close();
        }
        app = null;
    }

    @Test
    public void openGraph() throws ConfigurationException {
        assertNotNull(g);
    }

    @Test
    public void createElements() throws ConfigurationException {
        app.createElements();

        assertEquals(12L, g.V().count().next().longValue());
        assertEquals(1L, g.V().hasLabel("titan").count().next().longValue());
        assertEquals(1L, g.V().hasLabel("demigod").count().next().longValue());
        assertEquals(1L, g.V().hasLabel("human").count().next().longValue());
        assertEquals(3L, g.V().hasLabel("location").count().next().longValue());
        assertEquals(3L, g.V().hasLabel("god").count().next().longValue());
        assertEquals(3L, g.V().hasLabel("monster").count().next().longValue());

        assertEquals(17L, g.E().count().next().longValue());
        assertEquals(2L, g.E().hasLabel("father").count().next().longValue());
        assertEquals(1L, g.E().hasLabel("mother").count().next().longValue());
        assertEquals(6L, g.E().hasLabel("brother").count().next().longValue());
        assertEquals(1L, g.E().hasLabel("pet").count().next().longValue());
        assertEquals(4L, g.E().hasLabel("lives").count().next().longValue());
        assertEquals(3L, g.E().hasLabel("battled").count().next().longValue());
        final float[] place = (float[]) g.V().has("name", "hercules").outE("battled").has("time", 12).values("place")
                .next();
        assertNotNull(place);
        assertEquals(2, place.length);
        assertEquals(Float.valueOf(39f), Float.valueOf(place[0]));
        assertEquals(Float.valueOf(22f), Float.valueOf(place[1]));
    }

    @Test
    public void updateElements() throws ConfigurationException {
        app.createElements();
        assertFalse(g.V().has("name", "jupiter").has("ts").hasNext());
        app.updateElements();
        final long ts1 = (long) g.V().has("name", "jupiter").values("ts").next();
        app.updateElements();
        final long ts2 = (long) g.V().has("name", "jupiter").values("ts").next();
        assertTrue(ts2 > ts1);
    }

    @Test
    public void deleteElements() {
        app.createElements();
        app.deleteElements();
        assertFalse(g.V().has("name", "pluto").hasNext());
        assertFalse(g.V().has("name", "jupiter").both("brother").has("name", "pluto").hasNext());
    }
}