package org.cybnity.framework;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Custom implementation of configuration properties provider based on a read file.
 */
public class FileBasedConfigurationSource implements ConfigurationSource {

    /**
     * Configuration file path.
     */
    private final String filePath;

    /**
     * Default constructor.
     *
     * @param filePath Mandatory path to configuration file to read.
     * @throws IllegalArgumentException When missing parameter.
     */
    public FileBasedConfigurationSource(String filePath) throws IllegalArgumentException {
        if (filePath == null || filePath.isBlank())
            throw new IllegalArgumentException("filePath parameter is required and cannot be blank!");
        this.filePath = filePath;
    }

    /**
     * Get configuration resources.
     * Dynamic (also called "hot" change support) reading of origin file each time this method is called (allowing eventual changed configuration file values read).
     *
     * @return A set of properties and values, or null.
     * @throws UnoperationalStateException When configuration file read is impossible for any cause.
     */
    @Override
    public Map<String, String> getProperties() throws UnoperationalStateException {
        try {
            FileInputStream file = new FileInputStream(filePath);
            Properties prop = new Properties();
            // Load from file
            prop.load(file);
            file.close(); // Free resource (allowing unblocking of file after read)
            if (!prop.isEmpty())
                return prop.entrySet().stream().collect(Collectors.toMap(e -> String.valueOf(e.getKey()), e -> String.valueOf(e.getValue()), (prev, next) -> next, HashMap::new));
            return null;
        } catch (Exception e) {
            throw new UnoperationalStateException(e);
        }
    }
}
