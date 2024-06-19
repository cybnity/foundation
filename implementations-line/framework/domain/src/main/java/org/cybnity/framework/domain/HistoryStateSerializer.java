package org.cybnity.framework.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cybnity.framework.immutable.HistoryState;

import java.io.IOException;

/**
 * Custom serializer of enumeration regarding history status.
 */
public class HistoryStateSerializer extends StdSerializer<HistoryState> {
    public HistoryStateSerializer(Class<HistoryState> t) {
        super(t);
    }

    public HistoryStateSerializer() {
        this(null);
    }

    @Override
    public void serialize(HistoryState historyState, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("name");
        jsonGenerator.writeString(historyState.name());
        jsonGenerator.writeEndObject();
    }
}
