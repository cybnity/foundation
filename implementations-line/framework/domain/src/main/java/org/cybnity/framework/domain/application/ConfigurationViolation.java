package org.cybnity.framework.domain.application;

/**
 * Referential of setting violation causes detected by a process (e.g a missing configuration entry).
 * Type of violation helping the identification of configuration problem and the analysis of detected potential causes (e.g development quality issue, non conformity of packaging project relative to resource setting).
 */
public enum ConfigurationViolation {

    /**
     * Invalid configuration relative to a dependency that is missing.
     */
    MISSING_DEPENDENCY_ARTIFACT

    ;
}
