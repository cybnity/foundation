package org.cybnity.framework.domain.model;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cybnity.framework.domain.SerializationFormat;
import org.cybnity.framework.immutable.Identifier;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
    public DomainEntity deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SerializationFormat.DATE_FORMAT_PATTERN);
            ZonedDateTime d = ZonedDateTime.parse(createdAt, formatter);
            creationDate = d.toOffsetDateTime();
        }

        DomainEntity entity = new DomainEntity(identifiers);
        entity.setCreatedAt(creationDate);
        return entity;
    }
}
