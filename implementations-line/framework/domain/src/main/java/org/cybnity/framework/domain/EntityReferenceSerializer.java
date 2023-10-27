package org.cybnity.framework.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cybnity.framework.immutable.*;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.HashSet;

/**
 * Custom serializer of any type of EntityReference object.
 */
public class EntityReferenceSerializer extends StdSerializer<EntityReference> {
    public EntityReferenceSerializer(Class<EntityReference> t) {
        super(t);
    }

    public EntityReferenceSerializer() {
        this(null);
    }

    @Override
    public void serialize(EntityReference entityReference, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        // Add type info
        jsonGenerator.writeStringField("@type", entityReference.getClass().getSimpleName());

        try {
            Entity entity = entityReference.getEntity();
            if (entity != null) {
                jsonGenerator.writeObjectField("entity", entity);
            }
            Entity relation = entityReference.getReferencedRelation();
            if (relation != null) {
                jsonGenerator.writeObjectField("referencedRelation", relation);
            }
        } catch (ImmutabilityException ie) {
            throw new IOException(ie);
        }

        HistoryState historyStatus = entityReference.historyStatus();
        if (historyStatus != null) {
            jsonGenerator.writeObjectField("historyStatus", historyStatus);
        }

        OffsetDateTime changedAt = entityReference.occurredAt();
        if (changedAt != null) {
            jsonGenerator.writeObjectField("changedAt", changedAt);
        }
        jsonGenerator.writeEndObject();
    }
}
