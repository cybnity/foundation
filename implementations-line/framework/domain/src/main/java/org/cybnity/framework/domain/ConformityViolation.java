package org.cybnity.framework.domain;

/**
 * Referential of conformity violation causes detected by a process (e.g an entrypoint about invalid received event).
 * Type of violation helping the identification of conformity problem and the analysis of detected potential causes (e.g security attack, development quality issue).
 */
public enum ConformityViolation {

    /**
     * Type of fact event is not identified.
     */
    UNIDENTIFIED_EVENT_TYPE,

    /**
     * A message structure is not supported (e.g does not satisfy the JSON/POJO mapping requirements).
     */
    UNSUPPORTED_MESSAGE_STRUCTURE

    ;
}
