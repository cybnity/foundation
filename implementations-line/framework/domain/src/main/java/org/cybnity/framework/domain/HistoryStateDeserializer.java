package org.cybnity.framework.domain;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cybnity.framework.immutable.HistoryState;

import java.io.IOException;

/**
 * Customer deserializer of History state enum.
 */
public class HistoryStateDeserializer extends StdDeserializer<HistoryState> {
    public HistoryStateDeserializer(Class<?> vc) {
        super(vc);
    }

    public HistoryStateDeserializer() {
        this(null);
    }

    @Override
    public HistoryState deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String stateName = node.get("name").asText();
        for (HistoryState state : HistoryState.values()) {
            if (stateName.equalsIgnoreCase(state.name())) {
                return state;
            }
        }
        return null;
    }
}
