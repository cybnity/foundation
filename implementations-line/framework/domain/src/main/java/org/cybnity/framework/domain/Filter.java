package org.cybnity.framework.domain;

import java.util.Map;

/**
 * Filtering contract.
 *
 * @param <T> Type of elements container that shall be filtered.
 */
public interface Filter<T> {

    /**
     * Apply filtering conditions equals to specific parameters.
     *
     * @param toEvaluate        Elements container to evaluate by this filter.
     * @param selectionCriteria A set of parameters defining the condition of filtering of elements that shall be returned.
     * @param ctx               Optional context.
     * @return Filtered result(s) or null.
     */
    public T apply(T toEvaluate, Map<String, String> selectionCriteria, ISessionContext ctx);
}
