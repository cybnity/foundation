package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis.filter;

import io.lettuce.core.StreamMessage;
import org.cybnity.framework.domain.Filter;
import org.cybnity.framework.domain.ISessionContext;
import org.cybnity.infrastructure.technical.message_bus.adapter.api.Stream;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Filter of stream message elements that have equals specification attribute value.
 */
public class MessageSpecificationEqualsFilter implements Filter<List<StreamMessage<String, String>>> {

    /**
     * Set of parameter types which are supported by this filter.
     */
    public enum FilteringCriteria {

        /**
         * Name of criteria relative to an origin subject identifier.
         * The param name value is equals to a specification attribute name used into a stream as queryable (e.g message attribute).
         */
        ORIGIN_SUBJECT_ID_PARAM_NAME(/* Attribute of message supported by a readable stream */Stream.Specification.ORIGIN_SUBJECT_ID_KEY_NAME.name());

        private final String paramName;

        FilteringCriteria(String paramName) {
            this.paramName = paramName;
        }

        public String paramName() {
            return this.paramName;
        }
    }

    /**
     * Filtering implementation method that return elements where origin subject have equals identifier.
     */
    @Override
    public List<StreamMessage<String, String>> apply(List<StreamMessage<String, String>> toEvaluate, Map<String, String> selectionCriteria, ISessionContext ctx) {
        if (selectionCriteria != null && toEvaluate != null) {
            // Read each query parameter which shall be used as comparator during evaluation
            String originSubjectIDCriteria = selectionCriteria.getOrDefault(FilteringCriteria.ORIGIN_SUBJECT_ID_PARAM_NAME.paramName(), null);

            if (originSubjectIDCriteria != null && !originSubjectIDCriteria.isEmpty()) {
                List<StreamMessage<String, String>> filtered = new LinkedList<>();
                Map<String, String> messageBody;
                String originSubjectID;
                // Apply the filter rule and retain only the elements that have equals identifier
                for (StreamMessage<String, String> item : toEvaluate) {
                    if (item != null) {
                        // --- FILTER: EXECUTE CONDITION EVALUATION
                        // Read the origin subject identifier when existing
                        messageBody = item.getBody();
                        // Read optional subject's unique identifier (e.g domain event identifier, or aggregate identifier)
                        originSubjectID = messageBody.get(Stream.Specification.ORIGIN_SUBJECT_ID_KEY_NAME.name());
                        if (originSubjectID != null) {
                            // Verify if equals identifier with filtering condition
                            if (originSubjectIDCriteria.equals(originSubjectID)) {
                                // Select item that is about equals origin subject
                                filtered.add(item);
                            }
                        }/*
                         * else unknown element attribute, making impossible evaluation...
                         * So does not retain the element!
                         */
                    }
                }
                // Return filtered elements container
                return filtered;
            }
        }
        // Return original elements without filtered contents
        return toEvaluate;
    }
}
