package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.StreamMessage;
import org.cybnity.framework.domain.IDescribed;
import org.cybnity.framework.domain.ObjectMapperBuilder;
import org.cybnity.framework.domain.event.ProcessingUnitPresenceAnnounced;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MappingException;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MessageMapper;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.Stream;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapper of data structure between StreamMessage and event type.
 */
public class StreamMessageToProcessingUnitPresenceAnnouncedTransformer implements MessageMapper {

    private IDescribed result;

    /**
     * Default constructor.
     */
    public StreamMessageToProcessingUnitPresenceAnnouncedTransformer() {
    }

    @Override
    public void transform(Object origin) throws IllegalArgumentException, MappingException {
        if (origin == null) throw new IllegalArgumentException("Origin parameter is required!");
        if (!StreamMessage.class.isAssignableFrom(origin.getClass()) && !HashMap.class.isAssignableFrom(origin.getClass())) {
            throw new IllegalArgumentException("Origin parameter type is not supported by this mapper!");
        }
        try {
            Map<String,String> messageBody;
            if (StreamMessage.class.isAssignableFrom(origin.getClass())) {
                StreamMessage message = (StreamMessage) origin;
                // Read message body
                messageBody = message.getBody();
            } else {
                messageBody = (Map<String,String>) origin;
            }
            // Read map entries regarding unique identifier of fact record (streams partitioning based on keys)
            //String streamEntryID = (String) messageBody.get(Stream.Specification.FACT_RECORD_ID_KEY_NAME.name());

            // Read the payload message equals to fact body in a JSON formatted value
            String sourceEventJSON = messageBody.get(Stream.Specification.MESSAGE_PAYLOAD_KEY_NAME.name());

            // Select JSON transformation mapper allowing JSON deserialization of message payload
            ObjectMapper mapper = new ObjectMapperBuilder()
                    .enableIndentation()
                    .dateFormat()
                    .preserveOrder(true)
                    .build();

            // Restore the original event including all its internal attributes from JSON version
            // Attempt to read as Command
            result = mapper.readerFor(ProcessingUnitPresenceAnnounced.class).readValue(sourceEventJSON);
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    /**
     * Get the result as ProcessingUnitPresenceAnnounced instance.
     *
     * @return ProcessingUnitPresenceAnnounced event type.
     */
    @Override
    public Object getResult() {
        return this.result;
    }
}
