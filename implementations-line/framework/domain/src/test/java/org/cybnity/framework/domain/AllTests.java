package org.cybnity.framework.domain;

import org.cybnity.framework.domain.model.DomainEventUseCaseTest;
import org.cybnity.framework.domain.model.EventStoreUseCaseTest;
import org.cybnity.framework.domain.model.UserAccountAggregateStoreUseCaseTest;
import org.cybnity.framework.domain.model.UserAccountCQRSCollaborationUseCaseTest;
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
@SuiteClasses({ DomainEventUseCaseTest.class, NotificationLogUseCaseTest.class,
	UnidentifiableFactNotificationLogUseCaseTest.class, EventStoreUseCaseTest.class, ContextUseCaseTest.class,
	UserAccountAggregateStoreUseCaseTest.class, UserAccountCQRSCollaborationUseCaseTest.class })
public class AllTests {
}