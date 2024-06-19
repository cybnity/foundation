package org.cybnity.framework.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cybnity.framework.immutable.persistence.FactType;

import java.io.IOException;

/**
 * Custom serializer of any type of FactType object.
 */
public class FactTypeSerializer extends StdSerializer<FactType> {
    public FactTypeSerializer(Class<FactType> t) {
        super(t);
    }

    public FactTypeSerializer() {
        this(null);
    }

    @Override
    public void serialize(FactType instance, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        // Add type info
        jsonGenerator.writeStringField("@class", instance.getClass().getSimpleName());

        // Add mandatory category name required for by future deserialization process
        String name = instance.name();
        if (name == null || name.isEmpty())
            throw new IOException("name attribute is required from instance to serialize!");
        jsonGenerator.writeObjectField("name", name);

        // Read optional identifier
        String id = instance.id();
        if (id != null && !id.isEmpty()) {
            jsonGenerator.writeObjectField("id", id);
        }

        jsonGenerator.writeEndObject();
    }
}
