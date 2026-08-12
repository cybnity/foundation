package org.cybnity.framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom implementation of configuration properties provider based on multiple subsequent configuration sources.
 */
public class MergedConfigurationSource implements ConfigurationSource {

    List<ConfigurationSource> sources;

    /**
     * Default constructor.
     *
     * @param sources Mandatory subsequent configuration sources to merge.
     * @throws IllegalArgumentException When missing parameter.
     */
    public MergedConfigurationSource(List<ConfigurationSource> sources) throws IllegalArgumentException {
        if (sources == null) {
            throw new IllegalArgumentException("sources paramert is required!");
        }
        this.sources = sources;
    }

    /**
     * Get configuration resources.
     *
     * @return A set of properties and values, or null.
     * @throws UnoperationalStateException When impossible providing of properties (e.g; file based source read problem).
     */
    @Override
    public Map<String, String> getProperties() throws UnoperationalStateException {
        Map<String, String> mergedProp = new HashMap<>();
        for (ConfigurationSource source : sources) {
            Map<String, String> sourceProperties = source.getProperties();
            mergedProp.putAll(sourceProperties);
        }
        if (!mergedProp.isEmpty())
            return mergedProp;
        return null;
    }

}
