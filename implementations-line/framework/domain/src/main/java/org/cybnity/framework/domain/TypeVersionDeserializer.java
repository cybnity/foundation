package org.cybnity.framework.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cybnity.framework.immutable.persistence.FactType;
import org.cybnity.framework.immutable.persistence.TypeVersion;

import java.io.IOException;

/**
 * Custom deserializer of type version supporting multiple instantiation parameters.
 */
public class TypeVersionDeserializer extends StdDeserializer<TypeVersion> {
    public TypeVersionDeserializer(Class<?> vc) {
        super(vc);
    }

    public TypeVersionDeserializer() {
        this(null);
    }

    @Override
    public TypeVersion deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        try {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            // Read mandatory hash value
            String subjectHashValue = node.get("hash").asText();

            // Read mandatory fact type
            JsonNode type = node.get("factType");
            FactType subject = deserializationContext.readTreeAsValue(type, FactType.class);

            // Read optional id
            JsonNode id = node.get("id");
            String identifier = null;
            if (id != null) identifier = id.asText();

            return new TypeVersion(subject, subjectHashValue, identifier);
        } catch (NullPointerException npe) {
            throw new IOException(npe);
        }
    }
}
