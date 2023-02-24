package org.cybnity.framework.domain;

import org.cybnity.framework.domain.model.DomainEventUseCaseTest;
import org.cybnity.framework.domain.model.application.EventStoreUseCaseTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Suite of all technical and behavior tests regarding the domain components
 * capabilities.
 * 
 * @author olivier
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ DomainEventUseCaseTest.class, EventStoreUseCaseTest.class })
public class AllTests {
}