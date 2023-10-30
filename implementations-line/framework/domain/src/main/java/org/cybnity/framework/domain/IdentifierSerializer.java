package org.cybnity.framework.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cybnity.framework.immutable.Identifier;

import java.io.IOException;

/**
 * Custom serializer of any type of Identifier object.
 */
public class IdentifierSerializer extends StdSerializer<Identifier> {
    public IdentifierSerializer(Class<Identifier> t) {
        super(t);
    }

    public IdentifierSerializer() {
        this(null);
    }

    @Override
    public void serialize(Identifier identifier, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        // Add type info
        jsonGenerator.writeStringField("@type", identifier.getClass().getSimpleName());
        jsonGenerator.writeStringField("name", identifier.name());
        jsonGenerator.writeStringField("value", identifier.value().toString());
        jsonGenerator.writeEndObject();
    }
}
