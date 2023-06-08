package org.cybnity.framework.domain.model.sample.readmodel;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.application.sample.UserAccountAggregate;
import org.cybnity.framework.domain.model.CommonChildFactImpl;
import org.cybnity.framework.domain.model.DomainEventPublisher;
import org.cybnity.framework.domain.model.DomainEventSubscriber;
import org.cybnity.framework.domain.model.Repository;
import org.cybnity.framework.domain.model.sample.ApplicativeRole;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountChanged;
import org.cybnity.framework.domain.model.sample.writemodel.UserAccountStore;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.ImmutabilityException;

/**
 * Example of simple in-memory store of instances regarding user account (domain
 * object) type, in a destructured representation allowing their read as
 * ReadModel.
 * 
 * @author olivier
 *
 */
public class UserAccountRepository extends Repository {

	/**
	 * Registries per type of managed dto object (key=account identifier value,
	 * value=history of account)
	 */
	private ConcurrentHashMap<String, LinkedList<UserAccountDTO>> registries = new ConcurrentHashMap<String, LinkedList<UserAccountDTO>>();

	/**
	 * Delegated capability regarding promotion of ReadModel changes.
	 */
	private DomainEventPublisher domainEventsManager;

	/**
	 * Write model store to monitoring regarding the updated user accounts.
	 */
	private UserAccountStore writeModelStore;

	private DomainEventSubscriber<UserAccountChanged> writeModelCreatedAccountListener = new DomainEventSubscriber<UserAccountChanged>() {
		@Override
		public void handleEvent(UserAccountChanged event) {
			try {
				// Refresh the read model regarding the new state of created user account
				System.out.println("Start read model refresh about new created user account (useraccountaggregate.id="
						+ event.createdAccountRef.getEntity().identified().value().toString() + ")");

				// Find last version of the user account from write model's store
				// and replace the user account (if exist) of the ReadModel store by the new
				// version
				// of the stored UserAccountAggregate
				UserAccountAggregate existInstance = writeModelStore
						.findFrom((String) event.createdAccountRef.getEntity().identified().value());
				if (existInstance != null) {
					append(existInstance, event.creationCommandRef);

					// Read model is up-to-date
					System.out.println(UserAccountRepository.class.getSimpleName()
							+ " successfully refreshed with last version of user account (accountId="
							+ event.createdAccountRef.getEntity().identified().value().toString() + ")");
				}
			} catch (Exception e) {
				// Log impossible identification of changed information
				e.printStackTrace();
			}
		}

		@Override
		public Class<?> subscribeToEventType() {
			return UserAccountChanged.class;
		}
	};

	/**
	 * Default constructor of repository with automatic subscription to write model
	 * store regarding the user account aggregates changes.
	 * 
	 * @param observedWriteModel Mandatory observed account stores.
	 * @throws IllegalArgumentException When mandatory parameter is missing.
	 */
	private UserAccountRepository(UserAccountStore observedWriteModel) throws IllegalArgumentException {
		super();
		if (observedWriteModel == null)
			throw new IllegalArgumentException("observedWriteModel parameter is required!");
		this.writeModelStore = observedWriteModel;

		// Initialize a delegate for changes management (e.g to user
		// interfaces)
		this.domainEventsManager = DomainEventPublisher.instance();

		// Registering of domain events changes from WriteModel
		this.writeModelStore.subscribe(this.writeModelCreatedAccountListener);
	}

	/**
	 * Get an instance of the repository, ready for operating (e.g configured).
	 * 
	 * @param observedWriteModel Mandatory store to monitor regarding the WriteModel
	 *                           managing the persisted UserAccountAggregate.
	 * @return An instance ensuring the read of user account states.
	 */
	public static UserAccountRepository instance(UserAccountStore observedWriteModel) {
		return new UserAccountRepository(observedWriteModel);
	}

	/**
	 * Search in store from identifier value.
	 * 
	 * @param uid Mandatory identifier of the user account to find.
	 * @return Found account or null.
	 */
	public UserAccountDTO findFrom(String uid) {
		if (uid != null && !uid.equals("")) {
			// Search existing account column for the identifier value
			LinkedList<UserAccountDTO> storeAccountColumn = this.registries.get(uid);
			if (storeAccountColumn != null) {
				// Find the last version of the object
				Iterator<UserAccountDTO> it = storeAccountColumn.descendingIterator();
				while (it.hasNext()) {
					UserAccountDTO historizedVersion = it.next();
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
			this.domainEventsManager.subscribe(aSubscriber);
	}

	@Override
	public <T> void remove(DomainEventSubscriber<T> aSubscriber) {
		if (aSubscriber != null)
			this.domainEventsManager.remove(aSubscriber);
	}

	/**
	 * Add version of a user account denormalized dto into its registry column.
	 * Notify automatically the potential observers of changed account version.
	 * 
	 * @param account          Mandatory account to add.
	 * @param transactionOrder Mandatory origin command reference for add.
	 * @throws IllegalArgumentException When mandatory parameter is missing.
	 * @throws ImmutabilityException    Whe problem of copy regarding immutable
	 *                                  version of account parameter.
	 */
	private void append(UserAccountAggregate account, EntityReference transactionOrder)
			throws IllegalArgumentException, ImmutabilityException {
		if (account == null)
			throw new IllegalArgumentException("Account parameter is required!");
		if (transactionOrder == null)
			throw new IllegalArgumentException("Transaction parameter is required!");
		try {
			// Serialize the account to store into the storage system (generally according
			// to a serializer supported by the persistence system as JSON, table
			// structure's field...)
			UserAccountDTO createdVersion = buildFrom(account);

			// Find existing history regarding the account id, or initialize empty dataset
			// for the type
			String dtoUid = (String) createdVersion.getUserAccountEntityIdentifier().identified().value();
			LinkedList<UserAccountDTO> eventTypeDataset = this.registries.getOrDefault(dtoUid,
					new LinkedList<UserAccountDTO>());
			// Add the object version to the end of history column regarding its uid
			eventTypeDataset.add(createdVersion);
			// Save object version in registry's column
			registries.put(dtoUid, eventTypeDataset);

			// Build child event based on the created account (parent of immutable story)
			CommonChildFactImpl persistedVersion = new CommonChildFactImpl(
					createdVersion.getUserAccountEntityIdentifier(),
					new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(),
							/* identifier as performed transaction number */ UUID.randomUUID().toString()));
			UserAccountDTOChanged committedAccount = new UserAccountDTOChanged(persistedVersion.parent());
			committedAccount.creationCommandRef = transactionOrder;
			committedAccount.createdAccountDTORef = createdVersion.getUserAccountEntityIdentifier().reference();

			// Notify listeners of this store
			this.domainEventsManager.publish(committedAccount);
		} catch (NullPointerException npe) {
			throw new IllegalArgumentException(npe);
		}
	}

	/**
	 * Build a denormalized version of an account.
	 * 
	 * @param account Mandatory domain object to read.
	 * @return Denormalized instance representing the domain object and that is
	 *         supported by storage system of this repository.
	 * @throws ImmutabilityException    When problem of immutable contents
	 *                                  instantiation during read of account
	 *                                  aggregate.
	 * @throws IllegalArgumentException When mandatory parameter is missing.
	 */
	private UserAccountDTO buildFrom(UserAccountAggregate account)
			throws ImmutabilityException, IllegalArgumentException {
		if (account == null)
			throw new IllegalArgumentException("Account parameter is required!");
		// Build roles denormalized version set
		Set<ApplicativeRole> assignedRoles = account.assignedRoles();
		Set<ApplicativeRoleDTO> roles = null;
		if (assignedRoles != null && !assignedRoles.isEmpty()) {
			roles = new HashSet<>(assignedRoles.size());
			for (ApplicativeRole role : account.assignedRoles()) {
				// Read description and create dto version of role
				if (role != null) {
					roles.add(new ApplicativeRoleDTO(role.getName()));
				}
			}
		}
		// Build read model object identity
		DenormalizedEntityImpl dtoEntity = new DenormalizedEntityImpl(new IdentifierStringBased(
				BaseConstants.IDENTIFIER_ID.name(), /*
													 * DTO identity in read model is equals to the original
													 * UserAccountAggregate identifier (of write model)
													 */ (String) account.identified().value()));

		return new UserAccountDTO(/* build account id denormalized version */dtoEntity,
				/* Read date of account version changed */ account.identity().occurredAt(), roles);
	}

}
