package org.cybnity.tool.test.sample;

import org.cybnity.framework.domain.model.IDomainModel;

/**
 * Example of an application domain.
 */
public class SampleDomain implements IDomainModel {
    @Override
    public String domainName() {
        return "sd";
    }
}
