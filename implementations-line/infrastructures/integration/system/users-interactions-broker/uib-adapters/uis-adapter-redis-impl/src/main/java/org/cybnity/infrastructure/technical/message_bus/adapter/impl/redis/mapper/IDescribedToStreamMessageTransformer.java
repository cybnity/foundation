package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cybnity.framework.domain.IDescribed;
import org.cybnity.framework.domain.ObjectMapperBuilder;
import org.cybnity.framework.domain.event.HydrationAttributeProvider;
import org.cybnity.framework.immutable.IdentifiableFact;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MappingException;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MessageMapper;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.Stream;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapper of data structure between event and Map<String, String> type.
 */
public class IDescribedToStreamMessageTransformer implements MessageMapper {

    private Map<String, String> result;

    /**
     * Default constructor.
     */
    public IDescribedToStreamMessageTransformer() {
    }

    @Override
    public void transform(Object origin) throws IllegalArgumentException, MappingException {
        if (origin == null) throw new IllegalArgumentException("Origin parameter is required!");
        if (!IDescribed.class.isAssignableFrom(origin.getClass()))
            throw new IllegalArgumentException("Origin parameter type is not supported by this mapper!");
        try {
            IDescribed source = (IDescribed) origin; // can be a fact entity (e.g command, domain event) or an entity (e.g domain aggregate instance)
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
            transformedAs.put(queryAttributeAboutFactBody(), sourceEventJSON);

            // Prepare a fact record structured to manage the persistence state of the origin event
            // with specific defined fact's identifier allowing streams partitioning based on keys
            if (IdentifiableFact.class.isAssignableFrom(source.getClass())) {
                IdentifiableFact sourceRef = (IdentifiableFact) source;
                Identifier refID = sourceRef.identified();
                if (refID!=null) {
                    // Map entry regarding unique identifier of fact record (streams partitioning based on keys)
                    transformedAs.put(queryAttributeAboutFactRecordID(), refID.value().toString());
                }
            }

            // Evaluate if it's a fact regarding an origin domain object to reference in prepared result
            if (HydrationAttributeProvider.class.isAssignableFrom(origin.getClass())) {
                HydrationAttributeProvider hydrationOriginObjectContainer = (HydrationAttributeProvider) origin;
                Identifier factOriginSubjectId = hydrationOriginObjectContainer.changeSourceIdentifier();
                if (factOriginSubjectId != null) {
                    // Map entry regarding unique identifier of origin subject (e.g domain event identifier, or aggregate identifier)
                    transformedAs.put(queryAttributeAboutOriginSubjectID(), factOriginSubjectId.value().toString());
                }
            }

            // Update the prepared result
            result = transformedAs;
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    /**
     * Get the key name defining a fact record identifier queryable on a IDescribed message mapped version.
     *
     * @return A key name.
     */
    public static String queryAttributeAboutFactRecordID() {
        return Stream.Specification.FACT_RECORD_ID_KEY_NAME.name();
    }

    /**
     * Get the key name defining a IDescribed object's JSON payload as readable on its message mapped version.
     *
     * @return A key name.
     */
    public static String queryAttributeAboutFactBody() {
        return Stream.Specification.MESSAGE_PAYLOAD_KEY_NAME.name();
    }

    /**
     * Get the key name defining a IDescribed object's logical identifier as queryable on its message mapped version.
     *
     * @return A key name.
     */
    public static String queryAttributeAboutOriginSubjectID() {
        return Stream.Specification.ORIGIN_SUBJECT_ID_KEY_NAME.name();
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
