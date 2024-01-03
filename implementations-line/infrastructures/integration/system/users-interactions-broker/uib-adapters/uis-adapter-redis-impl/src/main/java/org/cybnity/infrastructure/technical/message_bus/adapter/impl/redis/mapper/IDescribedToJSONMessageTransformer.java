package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cybnity.framework.domain.IDescribed;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.ObjectMapperBuilder;
import org.cybnity.framework.domain.model.CommonChildFactImpl;
import org.cybnity.framework.immutable.*;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MappingException;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MessageMapper;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.Stream;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;

/**
 * Mapper of data structure between event and JSON type.
 */
public class IDescribedToJSONMessageTransformer implements MessageMapper {

    private String result;

    /**
     * Default constructor.
     */
    public IDescribedToJSONMessageTransformer() {
    }

    @Override
    public void transform(Object origin) throws IllegalArgumentException, MappingException {
        if (origin == null) throw new IllegalArgumentException("Origin parameter is required!");
        if (!IDescribed.class.isAssignableFrom(origin.getClass()))
            throw new IllegalArgumentException("Origin parameter type is not supported by this mapper!");
        try {
            IDescribed source = (IDescribed) origin;
            // Delete potential previous prepared result
            result = null;

            // Select JSON transformation mapper allowing JSON serialization of message payload
            ObjectMapper domainObjectMapper = new ObjectMapperBuilder()
                    .enableIndentation()
                    .dateFormat()
                    .preserveOrder(true)
                    .build();

            // Create JSON version of the original event including all its internal attributes
            result = domainObjectMapper.writeValueAsString(source);
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    /**
     * Get transformed object as JSON string.
     *
     * @return A String instance.
     */
    @Override
    public Object getResult() {
        return result;
    }
}
