package org.cybnity.framework.immutable;

import java.io.Serializable;

/**
 * Represent a record providing documentary evidence of specific events (see
 * NIST SP 800-152). A chronological record of system activities that include
 * records of system accesses and operations performed in a given period.
 * 
 * @author olivier
 *
 */
public class AuditLog implements Unmodifiable, Serializable {

    private static final long serialVersionUID = 1L;

    public AuditLog() {
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	AuditLog copy = new AuditLog();
	return copy;
    }

}
