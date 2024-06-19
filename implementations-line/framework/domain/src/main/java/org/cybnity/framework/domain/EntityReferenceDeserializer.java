package org.cybnity.framework.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.HistoryState;

import java.io.IOException;
import java.time.OffsetDateTime;

/**
 * Custom deserializer of entity reference supporting multiple instantiation parameters.
 */
public class EntityReferenceDeserializer extends StdDeserializer<EntityReference> {
    public EntityReferenceDeserializer(Class<?> vc) {
        super(vc);
    }

    public EntityReferenceDeserializer() {
        this(null);
    }

    @Override
    public EntityReference deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        // Read owner identity
        JsonNode entity = node.get("entity");
        Entity referenceOwner = deserializationContext.readTreeAsValue(entity, Entity.class);

        // Read referencedRelation
        JsonNode referencedRelation = node.get("referencedRelation");
        Entity inRelationWith = null;
        if (referencedRelation != null) {
            inRelationWith = deserializationContext.readTreeAsValue(referencedRelation, Entity.class);
        }

        // Read history status
        JsonNode historyState = node.get("historyStatus");
        HistoryState status = null;
        if (historyState != null) {
            status = deserializationContext.readTreeAsValue(historyState, HistoryState.class);
        }

        // Read date of creation (formatted)
        String createdAt = node.get("changedAt").asText();
        OffsetDateTime creationDate = null;
        if (!"".equals(createdAt)) {
            creationDate = OffsetDateTime.parse(createdAt);
        }

        return new EntityReference(referenceOwner, inRelationWith, status, creationDate);
    }
}
