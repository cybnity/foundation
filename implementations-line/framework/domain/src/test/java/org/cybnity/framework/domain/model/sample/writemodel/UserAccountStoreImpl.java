package org.cybnity.framework.domain.model.sample.writemodel;

import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.application.sample.UserAccountAggregate;
import org.cybnity.framework.domain.model.CommonChildFactImpl;
import org.cybnity.framework.domain.model.DomainEventPublisher;
import org.cybnity.framework.domain.model.DomainEventSubscriber;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.ImmutabilityException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Example of simple in-memory store of user account aggregates.
 *
 * @author olivier
 */
public class UserAccountStoreImpl implements UserAccountStore {

    /**
     * Registries per type of stored domain objects (key=account identifier value,
     * value=history of account instance)
     */
    private final ConcurrentHashMap<String, LinkedList<UserAccountAggregate>> registries = new ConcurrentHashMap<String, LinkedList<UserAccountAggregate>>();

    private final Logger logger = Logger.getLogger(UserAccountStoreImpl.class.getName());

    /**
     * Delegated capability regarding promotion of stored account changes.
     */
    private final DomainEventPublisher promotionManager;

    private UserAccountStoreImpl() {
        super();
        // Initialize a delegate for promotion of events changes (e.g to read model's
        // repository)
        this.promotionManager = DomainEventPublisher.instance();
    }

    /**
     * Get an instance of the user account store, ready for operating (e.g
     * configured).
     *
     * @return An instance ensuring the persistence of account.
     */
    public static UserAccountStore instance() {
        return new UserAccountStoreImpl();
    }

    @Override
    public void append(UserAccountAggregate account, Command transactionOrder)
            throws IllegalArgumentException, ImmutabilityException {
        if (account == null)
            throw new IllegalArgumentException("Account parameter is required!");
        if (transactionOrder == null)
            throw new IllegalArgumentException("Transaction parameter is required!");
        try {
            // Serialize the account to store into the storage system (generally according
            // to a serializer supported by the persistence system as JSON, table
            // structure's field...)
            // Find existing history regarding the account id, or initialize empty dataset
            // for the type
            String accountUID = (String) account.identified().value();
            LinkedList<UserAccountAggregate> eventTypeDataset = this.registries.getOrDefault(accountUID,
                    new LinkedList<UserAccountAggregate>());
            // Add the object version to the end of history column regarding its uid
            eventTypeDataset.add(account);
            // Save object version in registry's column
            registries.put(accountUID, eventTypeDataset);

            logger.fine("UserAccountStore successfully updated with a new version of UserAccountAggregate (id="
                    + accountUID + ")");
            // Build event child based on the created account (parent of immutable story)
            CommonChildFactImpl persistedVersion = new CommonChildFactImpl(account.identity(),
                    new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(),
                            /* identifier as performed transaction number */ UUID.randomUUID().toString()));
            UserAccountChanged committedAccount = new UserAccountChanged(persistedVersion.parent());
            committedAccount.creationCommandRef = transactionOrder.reference();
            committedAccount.changedAccountRef = account.identity().reference();
            // Notify listeners of this store
            this.promotionManager.publish(committedAccount);
        } catch (NullPointerException npe) {
            throw new IllegalArgumentException(npe);
        }
    }

    /**
     * Search in store an event logged.
     *
     * @param uid Mandatory identifier of the object to find.
     * @return Found last version of object, or null.
     */
    public UserAccountAggregate findFrom(String uid) {
        if (uid != null && !uid.isEmpty()) {
            // Search existing account column for the identifier value
            LinkedList<UserAccountAggregate> storeAccountColumn = this.registries.get(uid);
            if (storeAccountColumn != null) {
                // Find the last version of the object
                Iterator<UserAccountAggregate> it = storeAccountColumn.descendingIterator();
                while (it.hasNext()) {
                    UserAccountAggregate historizedVersion = it.next();
                    if (historizedVersion != null)
                        return historizedVersion;
                }
            }
        }
        return null;
    }

    @Override
    public <T> void subscribe(DomainEventSubscriber<T> aSubscriber) {
        if (aSubscriber != null)
            // Add listener interested by stored events
            this.promotionManager.subscribe(aSubscriber);
    }

    @Override
    public <T> void remove(DomainEventSubscriber<T> aSubscriber) {
        if (aSubscriber != null)
            this.promotionManager.remove(aSubscriber);
    }

}
