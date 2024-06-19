package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cybnity.framework.domain.ObjectMapperBuilder;
import org.cybnity.framework.domain.model.EventRecord;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.IReferenceable;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MappingException;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MessageMapper;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.Stream;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapper of data structure between event and Map type.
 */
public class EventRecordToStreamMessageTransformer implements MessageMapper {

    private Map<String, String> result;

    /**
     * Default constructor.
     */
    public EventRecordToStreamMessageTransformer() {
    }

    @Override
    public void transform(Object origin) throws IllegalArgumentException, MappingException {
        if (origin == null) throw new IllegalArgumentException("Origin parameter is required!");
        if (!EventRecord.class.isAssignableFrom(origin.getClass()))
            throw new IllegalArgumentException("Origin parameter type is not supported by this mapper!");
        try {
            EventRecord source = (EventRecord) origin;
            // Delete potential previous prepared result
            result = null;

            // Select JSON transformation mapper allowing JSON serialization of message payload
            ObjectMapper domainObjectMapper = new ObjectMapperBuilder()
                    .enableIndentation()
                    .dateFormat()
                    .preserveOrder(true)
                    .build();

            // Create JSON version of the original event including all its internal attributes
            String sourceEventJSON = domainObjectMapper.writeValueAsString(source);

            // Prepare a target type of instance
            Map<String, String> transformedAs = new HashMap<>();
            // Map entry regarding the payload message equals to fact body in a JSON formatted value
            transformedAs.put(Stream.Specification.MESSAGE_PAYLOAD_KEY_NAME.name(), sourceEventJSON);

            Identifier originSubjectId = null;
            if (source.body() != null && IReferenceable.class.isAssignableFrom(source.body().getClass())) {
                IReferenceable originSubjectOfFactRef = (IReferenceable) source.body();
                EntityReference ref = originSubjectOfFactRef.reference();
                if (ref != null) {
                    Entity factBodyOriginObjectID = ref.getEntity();
                    if (factBodyOriginObjectID != null) {
                        // Read identifier of origin subject
                        originSubjectId = factBodyOriginObjectID.identified();
                        // Map entry regarding unique identifier of subject (e.g domain event identifier, or aggregate identifier)
                        transformedAs.put(Stream.Specification.ORIGIN_SUBJECT_ID_KEY_NAME.name(), originSubjectId.value().toString());
                    }
                }
            }

            // Basis identifier allowable to the fact record
            if (source.getFactId() != null)
                // Map entry regarding unique identifier of fact record (streams partitioning based on keys)
                transformedAs.put(Stream.Specification.FACT_RECORD_ID_KEY_NAME.name(), source.getFactId().toString());

            // Update the prepared result
            result = transformedAs;
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    /**
     * Get transformed object as Map.
     *
     * @return A String, String map instance including Stream.Specification.FACT_RECORD_ID_KEY_NAME.name() and Stream.Specification.MESSAGE_PAYLOAD_KEY_NAME.name() as key information.
     */
    @Override
    public Object getResult() {
        return result;
    }
}
