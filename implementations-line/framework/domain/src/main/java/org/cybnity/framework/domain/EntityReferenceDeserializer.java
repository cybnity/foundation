package org.cybnity.framework.domain;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.Identifier;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedHashSet;

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
    public EntityReference deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        ObjectMapper mapper = new ObjectMapperBuilder()
                .enableIndentation()
                .dateFormat()
                .preserveOrder(true)
                .build();
        // Read owner identity
        JsonNode entity = node.get("entity");
        Entity referenceOwner = deserializationContext.readTreeAsValue(entity, Entity.class);

        //Entity owner = mapper.readerFor(Entity.class).readValue(entity);

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
        String createdAt = node.get("createdAt").asText();
        OffsetDateTime creationDate = null;
        if (!"".equals(createdAt)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SerializationFormat.DATE_FORMAT_PATTERN);
            ZonedDateTime d = ZonedDateTime.parse(createdAt, formatter);
            creationDate = d.toOffsetDateTime();
        }

        return new EntityReference(referenceOwner, inRelationWith, status, creationDate);
    }
}
