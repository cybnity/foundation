package org.cybnity.framework.domain.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cybnity.framework.immutable.Identifier;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Custom deserializer of domain entity supporting multiple instantiation parameters.
 */
public class DomainEntityDeserializer extends StdDeserializer<DomainEntity> {
    public DomainEntityDeserializer(Class<?> vc) {
        super(vc);
    }

    public DomainEntityDeserializer() {
        this(null);
    }

    @Override
    public DomainEntity deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        // Read array of identifiers defining identity of entity
        LinkedHashSet<Identifier> identifiers = new LinkedHashSet<>();
        Iterator<JsonNode> it = node.withArray("identifiedBy").elements();

        while (it.hasNext()) {
            JsonNode el = it.next();
            // Create identifier instance
            Identifier implId = deserializationContext.readTreeAsValue(el, Identifier.class);
            identifiers.add(implId);
        }

        // Read date of creation (formatted)
        String createdAt = node.get("createdAt").asText();
        OffsetDateTime creationDate = null;
        if (!"".equals(createdAt)) {
            creationDate = OffsetDateTime.parse(createdAt);
        }

        DomainEntity entity = new DomainEntity(identifiers);
        entity.setCreatedAt(creationDate);
        return entity;
    }
}
