package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.StreamMessage;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.DomainEvent;
import org.cybnity.framework.domain.IDescribed;
import org.cybnity.framework.domain.ObjectMapperBuilder;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MappingException;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MessageMapper;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.Stream;

import java.util.Map;

/**
 * Mapper of data structure between StreamMessage and event type.
 */
public class StreamMessageToIDescribedTransformer implements MessageMapper {

    private IDescribed result;

    /**
     * Default constructor.
     */
    public StreamMessageToIDescribedTransformer() {
    }

    @Override
    public void transform(Object origin) throws IllegalArgumentException, MappingException {
        if (origin == null) throw new IllegalArgumentException("Origin parameter is required!");
        if (!StreamMessage.class.isAssignableFrom(origin.getClass()))
            throw new IllegalArgumentException("Origin parameter type is not supported by this mapper!");
        try {
            StreamMessage message = (StreamMessage) origin;

            // Read message body
            Map messageBody = message.getBody();
            // Read map entries regarding unique identifier of fact record (streams partitioning based on keys)
            //String streamEntryID = (String) messageBody.get(Stream.Specification.FACT_RECORD_ID_KEY_NAME.name());

            // Read the payload message equals to fact body in a JSON formatted value
            String sourceEventJSON = (String) messageBody.get(Stream.Specification.MESSAGE_PAYLOAD_KEY_NAME.name());

            // Select JSON transformation mapper allowing JSON deserialization of message payload
            ObjectMapper mapper = new ObjectMapperBuilder()
                    .enableIndentation()
                    .dateFormat()
                    .preserveOrder(true)
                    .build();

            // Restore the original event including all its internal attributes from JSON version
            try {
                // Attempt to read as Command
                result = mapper.readerFor(Command.class).readValue(sourceEventJSON);
            } catch (Exception me) {
                // Attempt to read as DomainEvent
                result = mapper.readerFor(DomainEvent.class).readValue(sourceEventJSON);
            }
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    /**
     * Get the result as IDescribed instance.
     *
     * @return IDescribed event type.
     */
    @Override
    public Object getResult() {
        return this.result;
    }
}
