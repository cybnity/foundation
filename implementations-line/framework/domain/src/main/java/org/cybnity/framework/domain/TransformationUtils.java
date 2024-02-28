package org.cybnity.framework.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cybnity.framework.immutable.Identifier;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class regarding transformation mechanisms.
 */
public class TransformationUtils {

    /**
     * Convert a list of identifies into a string version of array list identification values.
     *
     * @param identifiers Mandatory list to convert.
     * @return A string version of the identifiers array (e.g [xxxx-id-yyyy, aa-id-bbb]). Null when identifiers parameter is null or empty list.
     */
    public static String convert(List<Identifier> identifiers) {
        if (identifiers != null && !identifiers.isEmpty()) {
            List<String> streamValues = new ArrayList<>();
            for (Identifier id : identifiers) {
                streamValues.add(id.value().toString());
            }
            return Arrays.deepToString(streamValues.toArray());
        }
        return null;
    }

    /**
     * Convert date into a string value.
     *
     * @param date Mandatory date to convert.
     * @return A string version of the date. Null when date parameter is null.
     * @throws IllegalArgumentException When a problem of serialization into string is occurred.
     */
    public static String convert(OffsetDateTime date) throws IllegalArgumentException {
        if (date != null) {
            try {
                // Make conversion of offset aligned with the mapping rules used for data JSON serialization that allow future deserialization
                ObjectMapper mapper = new ObjectMapperBuilder()
                        .enableIndentation()
                        .dateFormat()
                        .preserveOrder(true)
                        .build();
                return mapper.writeValueAsString(date);
            } catch (JsonProcessingException jpe) {
                throw new IllegalArgumentException(jpe);
            }
        }
        return null;
    }
}
