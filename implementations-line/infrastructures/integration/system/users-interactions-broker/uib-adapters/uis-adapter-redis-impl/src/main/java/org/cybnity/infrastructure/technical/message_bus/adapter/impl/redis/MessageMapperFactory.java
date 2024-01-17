package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import io.lettuce.core.StreamMessage;
import org.cybnity.framework.domain.IDescribed;
import org.cybnity.framework.domain.event.ProcessingUnitPresenceAnnounced;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.IMessageMapperProvider;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.MessageMapper;
import org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.mapper.*;

/**
 * Utility class allowing to transform an object manageable by the space according to a type of data structure supported by Redis.
 * For example, translate a CommandEvent object into a String (message body).
 */
public class MessageMapperFactory implements IMessageMapperProvider {

    public MessageMapperFactory() {
    }

    @Override
    public MessageMapper getMapper(Class<?> transformable, Class<?> transformableAs) {
        if (transformable != null && transformableAs != null) {
            // Select the origin type to be transformed
            if (IDescribed.class.isAssignableFrom(transformable)) {
                // Select the provided mapper allowing transformation to targeted type
                if (StreamMessage.class.isAssignableFrom(transformableAs)) {
                    return new IDescribedToStreamMessageTransformer();
                } else if (String.class.isAssignableFrom(transformableAs)) {
                    return new IDescribedToJSONMessageTransformer();
                }
            } else if (StreamMessage.class.isAssignableFrom(transformable)) {
                // Select the mapper allowing transformation to targeted type
                if (ProcessingUnitPresenceAnnounced.class.isAssignableFrom(transformableAs)) {
                    return new StreamMessageToProcessingUnitPresenceAnnouncedTransformer();
                } else if (IDescribed.class.isAssignableFrom(transformableAs)) {
                    return new StreamMessageToIDescribedTransformer();
                }
            } else if (String.class.isAssignableFrom(transformable)) {
                // Select the mapper allowing transformation to targeted type
                if (ProcessingUnitPresenceAnnounced.class.isAssignableFrom(transformableAs)) {
                    return new JSONMessageToProcessingUnitPresenceAnnouncedTransformer();
                } else if (IDescribed.class.isAssignableFrom(transformableAs)) {
                    return new JSONMessageToIDescribedTransformer();
                }
            }
        }
        return null; // None supported types
    }
}
