package org.cybnity.framework.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cybnity.framework.immutable.persistence.FactType;

import java.io.IOException;

/**
 * Custom deserializer of fact type supporting multiple instantiation parameters.
 */
public class FactTypeDeserializer extends StdDeserializer<FactType> {
    public FactTypeDeserializer(Class<?> vc) {
        super(vc);
    }

    public FactTypeDeserializer() {
        this(null);
    }

    @Override
    public FactType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        try {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            // Read mandatory name
            String name = node.get("name").asText();

            // Read optional id
            JsonNode id = node.get("id");
            String identifier = null;
            if (id != null) identifier = id.asText();

            return new FactType(name, identifier);
        } catch (NullPointerException npe) {
            throw new IOException(npe);
        }
    }
}
