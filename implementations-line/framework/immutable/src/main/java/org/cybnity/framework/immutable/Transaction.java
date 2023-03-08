package org.cybnity.framework.immutable;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Capture a known state of an entity to perform an atomic unit of work.
 * 
 * When no additional itms can be added and no properties can be modified
 * regarding a change request (e.g Command regarding an entity), the Transaction
 * pattern takes advantage of immutability for business processing.
 * 
 * It records the informatiopn about a request for work in such a way that it
 * cannot be modified after work begins.
 * 
 * A Transaction identifies as a predecessor an entity that it is acting upon.
 * Whereas that entity was originally a strating point for children, mutable
 * properties, and other successors, the transaction seeks to lock it down by
 * inverting the predecessor/successor relationship.
 * 
 * Where Ownership placed the paretn as a predecessor of its children,
 * Transaction makes children predecessors of parents. Successors can be added
 * over time, but predecessors are immutable. Recording children as predecessors
 * prevents further creation of deletion.
 * 
 * The transaction also identifies the specific versions of Mutable Properties,
 * thaht become direct or indirect predecessors of the the transaction. Again,
 * the relationship is inverted so that any further modifications to those
 * properties do not affect the transaction.
 * 
 * When a user submit a transaction, it lock down its current state. No further
 * changes to the transaction can be made.
 * 
 * Once a transaction is recorded, subsequent changes to the entities or
 * properties will have no effect. All of the information in this transaction is
 * recorded in predecessor relationships (predecessors are immutable, so this
 * transaction if locked down). Any difference in predecessors such as
 * transaction items or property versions would necessarily result in different
 * facts.
 * 
 * A transaction if processed atomically (according to ACID model with
 * Atomicity, Consistency, Isolation and Durability) and processing begins with
 * a query for this transaction, not an item. Items will remain dorman until the
 * subsequent transaction arrives, at which time all items will take effect
 * simultaneously.
 * 
 * All necessary information shall be in tha transitive closure of this
 * transaction. Starting at the transaction fact, follow all predecessors. From
 * those facts, recursively follow their predecessors. The transitive closure is
 * the set of all facts thus visited.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Maintainability, reqId = "REQ_MAIN_5")
public class Transaction implements IHistoricalFact {

    private static final long serialVersionUID = 1L;

    /**
     * Predecessor entity of this transaction.
     */
    protected final Entity transactionParentContext;

    /**
     * When this fact was created or observed regarding the historized topic. This
     * creation date distingues among children within the same predecessor.
     */
    protected OffsetDateTime createdAt;

    /**
     * Specific versions of mutable properties that become direct or indirect
     * predecessors of this transaction.
     */
    private Set<TransactionItem> items;

    /**
     * Optional identifier of this transaction.
     */
    private Identifier transactionId;

    /**
     * Default constructor.
     * 
     * @param transactionParentContext Mandatory parent of this transaction context.
     * @param id                       Unique and optional identifier of this
     *                                 transaction.
     * @throws IllegalArgumentException When predecessor mandatory parameter is not
     *                                  defined or without defined identifier.
     */
    public Transaction(
	    @Requirement(reqType = RequirementCategory.Consistency, reqId = "REQ_CONS_3") Entity transactionParentContext,
	    Identifier id) throws IllegalArgumentException {
	if (transactionParentContext == null)
	    throw new IllegalArgumentException("transactionParentContext parameter is required!");
	// Check conformity of optional child identifier
	if (id != null && (id.name() == null || id.name().equals("") || id.value() == null)) {
	    throw new IllegalArgumentException("Identifier parameter's name and value is required!");
	}
	try {
	    // Check mandatory existent identifier of parent (child identifier based on its
	    // contribution)
	    Collection<Identifier> predecessorIdentifiers = transactionParentContext.identifiers();
	    if (transactionParentContext.identified() == null || predecessorIdentifiers == null
		    || predecessorIdentifiers.isEmpty())
		throw new IllegalArgumentException("The parent identifier(s) shall be existent!");
	    // Reference immutable copy of predecessor
	    this.transactionParentContext = (Entity) transactionParentContext.immutable();
	    // Get optional transaction identifier
	    this.transactionId = id;
	    // Create immutable time of this fact creation
	    this.createdAt = OffsetDateTime.now();
	} catch (ImmutabilityException cn) {
	    throw new IllegalArgumentException(cn);
	}
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
	Transaction copy = new Transaction(this.transactionParentContext(), this.transactionId());
	copy.createdAt = this.createdAt;
	copy.setItems(this.getItems());
	return copy;
    }

    /**
     * Location-independent unique identifier of this transaction.
     * 
     * @return Unique based identifier or null.
     * @throws ImmutabilityException When problem to create immutable copy of this
     *                               transaction identifier.
     */
    public Identifier transactionId() throws ImmutabilityException {
	if (this.transactionId != null)
	    return (Identifier) this.transactionId.immutable();
	return null;
    }

    /**
     * Default implementation of fact date when it was created.
     */
    @Override
    public OffsetDateTime occurredAt() {
	// Return the immutable value of the fact time
	return this.createdAt;
    }

    /**
     * Predecessor fact of this transaction context.
     * 
     * @return A predecessor of this transaction that is a global modification
     *         context.
     * @throws ImmutabilityException When impossible cloned instance of predecessor.
     */
    @Requirement(reqType = RequirementCategory.Consistency, reqId = "REQ_CONS_3")
    public Entity transactionParentContext() throws ImmutabilityException {
	// Return unmodifiable instance of predecessor
	return (Entity) this.transactionParentContext.immutable();
    }

    /**
     * Get the current items identified as specific versions of peroperties which
     * are direct of indirect predecessors of this transaction.
     * 
     * @return Identified items immutable version (captured state of the objects as
     *         they are known to that user at that time) or null.
     * @throws ImmutabilityException When impossible cloned instance of items.
     */
    public Set<TransactionItem> getItems() throws ImmutabilityException {
	LinkedHashSet<TransactionItem> copy = null;
	if (items != null) {
	    copy = new LinkedHashSet<>(items.size());
	    for (TransactionItem anItem : items) {
		copy.add(new TransactionItem(anItem.getItemContext(), anItem.getPropertyState()));
	    }
	}
	return copy;
    }

    /**
     * Define the items identified as specific versions of peroperties which are
     * direct of indirect predecessors of this transaction.
     * 
     * @param items A set of items (captured state of the objects as they are known
     *              to that user at that time).
     */
    public void setItems(Set<TransactionItem> items) {
	this.items = items;
    }

    /**
     * Implement the generation of version hash regarding this class type according
     * to a concrete strategy utility service.
     */
    @Override
    public String versionHash() {
	return new VersionConcreteStrategy().composeCanonicalVersionHash(getClass());
    }
}
