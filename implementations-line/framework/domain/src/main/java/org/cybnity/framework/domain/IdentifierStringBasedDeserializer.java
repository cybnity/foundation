package org.cybnity.framework.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cybnity.framework.immutable.Identifier;

import java.io.IOException;

/**
 * Customer deserializer of Identifier object.
 */
public class IdentifierStringBasedDeserializer extends StdDeserializer<Identifier> {
    public IdentifierStringBasedDeserializer(Class<?> vc) {
        super(vc);
    }

    public IdentifierStringBasedDeserializer() {
        this(null);
    }

    @Override
    public Identifier deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String name = node.get("name").asText();
        String value = node.get("value").asText();

        return new IdentifierStringBased(name, value);
    }
}
