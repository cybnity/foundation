package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.ISubscribable;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Storage location, usually considered a place of safety or preservation of the
 * items stored in it. Every persistent Aggregate type have a Repository.
 * Component of the Read Model, generally there is one-to-one relationship
 * between an Aggregate type and a Repository.
 * <p>
 * A repository can be collection-oriented design or persistence-oriented
 * design.
 * <p>
 * Implementation of a Repository is generally attached to a boundary (e.g
 * domain bounded context; or Aggregate type) and expose mainly read functions
 * allowing to observers (e.g application user interface) to retrieve objects
 * states (e.g last version of a domain event, an Aggregate, an historical
 * fact).
 * <p>
 * In a CQRS pattern approach, the ReadModel implemented by a Repository
 * implementation manage the subscription to changes observable from a
 * WriteModel that allow it to refresh its contents automatically with a last
 * current version of objects items in a destructured version.
 * <p>
 * The changes of a Repository are promoted to any observers registered (e.g
 * application user interface) as interested about new refreshed version of
 * items managed by this repository.
 * <p>
 * When a Repository implementation does not only represent a ReadModel but
 * have an approach of centralized persistence (e.g merging of ReadModel and
 * WriteModel into one persistence area) including also the WriteModel, its
 * interface of capabilities expose functions allowing to search, to add/append
 * object versions.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Scalability, reqId = "REQ_SCA_4")
public abstract class Repository implements ISubscribable {

    /**
     * Delegated capability regarding promotion of stored events changes.
     */
    private final DomainEventPublisher promotionManager;

    /**
     * Default constructor managing the store configuration during its
     * instantiation. To be defined by the child class implementing a storage
     * persistence system.
     * This constructor is ensuring the instantiation of the promotionManager component which is container of publishing of events.
     */
    protected Repository() {
        // Initialize a delegate for promotion of events changes
        this.promotionManager = DomainEventPublisher.instance();
    }

    /**
     * Get the promotion manager managing the notification of registered subscribers regarding any change performed by the store.
     *
     * @return A publisher.
     */
    protected DomainEventPublisher subscribersManager() {
        return this.promotionManager;
    }

    @Override
    public <T> void subscribe(IDomainEventSubscriber<T> aSubscriber) {
        if (aSubscriber != null)
            // Add listener interested by stored events
            subscribersManager().subscribe(aSubscriber);
    }

    @Override
    public <T> void remove(IDomainEventSubscriber<T> aSubscriber) {
        if (aSubscriber != null)
            subscribersManager().remove(aSubscriber);
    }
}
