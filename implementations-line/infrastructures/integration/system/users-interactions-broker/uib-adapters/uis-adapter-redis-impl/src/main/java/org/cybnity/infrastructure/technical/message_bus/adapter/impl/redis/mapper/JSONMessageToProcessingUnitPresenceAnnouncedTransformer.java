package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cybnity.framework.domain.IDescribed;
import org.cybnity.framework.domain.ObjectMapperBuilder;
import org.cybnity.framework.domain.event.ProcessingUnitPresenceAnnounced;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MappingException;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MessageMapper;

/**
 * Mapper of data structure between JSON and event type.
 */
public class JSONMessageToProcessingUnitPresenceAnnouncedTransformer implements MessageMapper {

    private IDescribed result;

    /**
     * Default constructor.
     */
    public JSONMessageToProcessingUnitPresenceAnnouncedTransformer() {
    }

    @Override
    public void transform(Object origin) throws IllegalArgumentException, MappingException {
        if (origin == null) throw new IllegalArgumentException("Origin parameter is required!");
        if (!String.class.isAssignableFrom(origin.getClass()))
            throw new IllegalArgumentException("Origin parameter type is not supported by this mapper!");
        try {
            // Read message body
            String sourceEventJSON = (String) origin;

            // Select JSON transformation mapper allowing JSON deserialization of message payload
            ObjectMapper mapper = new ObjectMapperBuilder()
                    .enableIndentation()
                    .dateFormat()
                    .preserveOrder(true)
                    .build();

            // Restore the original event including all its internal attributes from JSON version
            // Attempt to read
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
