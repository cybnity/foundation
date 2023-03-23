package org.cybnity.framework.domain;

import org.cybnity.framework.domain.model.DomainEventUseCaseTest;
import org.cybnity.framework.domain.model.EventStoreUseCaseTest;
import org.cybnity.framework.domain.model.StringBasedNaturalKeyBuilderUseCaseTest;
import org.cybnity.framework.domain.model.TenantUseCaseTest;
import org.cybnity.framework.domain.model.UserAccountAggregateStoreUseCaseTest;
import org.cybnity.framework.domain.model.UserAccountAggregateUseCaseTest;
import org.cybnity.framework.domain.model.UserAccountCQRSCollaborationUseCaseTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Suite of all technical and behavior tests regarding the domain components
 * capabilities.
 * 
 * @author olivier
 *
 */
@Suite
@SelectClasses({ IdentifierStringBasedUseCaseTest.class, ValueObjectUseCaseTest.class, DomainEventUseCaseTest.class,
	NotificationLogUseCaseTest.class, UnidentifiableFactNotificationLogUseCaseTest.class,
	EventStoreUseCaseTest.class, ContextUseCaseTest.class, UserAccountAggregateUseCaseTest.class,
	UserAccountAggregateStoreUseCaseTest.class, UserAccountCQRSCollaborationUseCaseTest.class,
	StringBasedNaturalKeyBuilderUseCaseTest.class, TenantUseCaseTest.class })
public class AllTests {
}