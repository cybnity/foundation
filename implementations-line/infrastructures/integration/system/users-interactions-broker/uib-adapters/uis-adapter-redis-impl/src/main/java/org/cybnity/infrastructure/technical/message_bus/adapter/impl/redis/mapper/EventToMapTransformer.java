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
 * Mapper of data structure between event and Map<String, String> type.
 */
public class EventToMapTransformer implements MessageMapper {

    private Map<String, String> result;

    /**
     * Default constructor.
     */
    public EventToMapTransformer() {
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
            String sourceEventJSON = domainObjectMapper.writeValueAsString(source);

            // Prepare a fact record structured to manage the persistence state of the origin event
            // with specific defined fact's identifier allowing streams partitioning based on keys

            // Basis identifier allowable to the fact record
            // Define an entry identifier into the stream (key to distribute the partitions based on specific entry ID for each stream)
            Identifier streamEntryID = new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(),
                    /* identifier as performed transaction number */ UUID.randomUUID().toString());

            if (IReferenceable.class.isAssignableFrom(origin.getClass())) {
                IReferenceable sourceRef = (IReferenceable) source;
                EntityReference ref = sourceRef.reference();
                if (ref != null) {
                    Entity factPredecessorID = ref.getEntity();
                    if (factPredecessorID != null) {
                        // Prepare combined identifiers
                        LinkedHashSet<Identifier> factRecordCombinedIdentificationSources = new LinkedHashSet<>();
                        // Add technical identifier of the stream entry to combine with the fact record predecessor id
                        factRecordCombinedIdentificationSources.add(streamEntryID);
                        // Create a child fact representing a derived event identified by auto-generated combined uid usable by stream partitioning
                        ChildFact factRecord = new CommonChildFactImpl(/* have origin event predecessor */factPredecessorID, factRecordCombinedIdentificationSources);
                        streamEntryID = factRecord.identified(); // Combined uid in place of basis fact record id
                    }
                }
            }

            // Prepare a target type of instance
            Map<String, String> transformedAs = new HashMap<>();
            // Map entry regarding unique identifier of fact record (streams partitioning based on keys)
            transformedAs.put(Stream.Specification.FACT_RECORD_ID_KEY_NAME.name(), streamEntryID.value().toString());
            // Map entry regarding the payload message equals to fact body in a JSON formatted value
            transformedAs.put(Stream.Specification.MESSAGE_PAYLOAD_KEY_NAME.name(), sourceEventJSON);

            // Update the prepared result
            result = transformedAs;
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    /**
     * Get transformed object as Map<String, String>.
     *
     * @return A Map<String, String> instance including Stream.Specification.FACT_RECORD_ID_KEY_NAME.name() and Stream.Specification.MESSAGE_PAYLOAD_KEY_NAME.name() as key informations.
     */
    @Override
    public Object getResult() {
        return result;
    }
}
