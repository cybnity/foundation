package org.cybnity.framework.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cybnity.framework.immutable.persistence.FactType;
import org.cybnity.framework.immutable.persistence.TypeVersion;

import java.io.IOException;

/**
 * Custom serializer of any type of TypeVersion object.
 */
public class TypeVersionSerializer extends StdSerializer<TypeVersion> {
    public TypeVersionSerializer(Class<TypeVersion> t) {
        super(t);
    }

    public TypeVersionSerializer() {
        this(null);
    }

    @Override
    public void serialize(TypeVersion instance, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        // Add type info
        jsonGenerator.writeStringField("@class", instance.getClass().getSimpleName());

        // Add mandatory hash value required for by future deserialization process
        String hash = instance.hash();
        if (hash == null || hash.isEmpty())
            throw new IOException("hash attribute is required from instance to serialize!");
        jsonGenerator.writeObjectField("hash", hash);

        // Read mandatory fact type
        FactType factType = instance.factType();
        if (factType == null)
            throw new IllegalArgumentException("fact type attribute is required from instance to serialize!");
        jsonGenerator.writeObjectField("factType", factType);

        // Read optional identifier
        String id = instance.id();
        if (id != null && !id.isEmpty()) {
            jsonGenerator.writeObjectField("id", id);
        }

        jsonGenerator.writeEndObject();
    }
}
