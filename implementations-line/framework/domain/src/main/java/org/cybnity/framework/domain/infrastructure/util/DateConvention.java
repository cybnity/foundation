package org.cybnity.framework.domain.infrastructure.util;

import org.cybnity.framework.domain.SerializationFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Convention relative to the translation of a date into or from a string version managed into the infrastructure layer.
 */
public class DateConvention extends AbstractDataViewConvention {

    /**
     * Get a standard date converter supporting a standard data format pattern supported by the registry's element.
     * Helpful to manage the translation and the read of date (e.g attribute of data view or graph vertex) into a read-model.
     *
     * @return A formatter based on default pattern (SerializationFormat.DATE_FORMAT_PATTERN) of CYBNITY domain framework relative to persistent object serialization model.
     */
    public static DateFormat dateFormatter() {
        return new SimpleDateFormat(SerializationFormat.DATE_FORMAT_PATTERN);
    }
}
