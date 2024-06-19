package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.StreamMessage;
import org.cybnity.framework.domain.ObjectMapperBuilder;
import org.cybnity.framework.domain.model.EventRecord;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MappingException;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MessageMapper;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.Stream;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapper of data structure between StreamMessage and event type.
 */
public class StreamMessageToEventRecordTransformer implements MessageMapper {

    private EventRecord result;

    /**
     * Default constructor.
     */
    public StreamMessageToEventRecordTransformer() {
    }

    @Override
    public void transform(Object origin) throws IllegalArgumentException, MappingException {
        if (origin == null) throw new IllegalArgumentException("Origin parameter is required!");
        if (!StreamMessage.class.isAssignableFrom(origin.getClass()) && !HashMap.class.isAssignableFrom(origin.getClass()))
            throw new IllegalArgumentException("Origin parameter type is not supported by this mapper!");
        try {
            Map<String, String> messageBody;
            if (StreamMessage.class.isAssignableFrom(origin.getClass())) {
                StreamMessage message = (StreamMessage) origin;
                // Read message body
                messageBody = message.getBody();
            } else {
                messageBody = (Map<String, String>) origin;
            }

            // Read map entries regarding unique identifier of fact record (streams partitioning based on keys)
            //String streamEntryID = (String) messageBody.get(Stream.Specification.FACT_RECORD_ID_KEY_NAME.name());

            // Read optional subject's unique identifier (e.g domain event identifier, or aggregate identifier)
            //String originSubjectID = messageBody.get(Stream.Specification.ORIGIN_SUBJECT_ID_KEY_NAME.name());

            // Read the payload message equals to fact body in a JSON formatted value
            String sourceEventJSON = messageBody.get(Stream.Specification.MESSAGE_PAYLOAD_KEY_NAME.name());

            // Select JSON transformation mapper allowing JSON deserialization of message payload
            ObjectMapper mapper = new ObjectMapperBuilder()
                    .enableIndentation()
                    .dateFormat()
                    .preserveOrder(true)
                    .build();

            // Attempt to read as EventRecord instance
            result = mapper.readerFor(EventRecord.class).readValue(sourceEventJSON);

        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    /**
     * Get the result as EventRecord instance.
     *
     * @return EventRecord event type.
     */
    @Override
    public Object getResult() {
        return this.result;
    }
}
