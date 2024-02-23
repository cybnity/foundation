package org.cybnity.framework.domain.model;

import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;

import java.util.HashMap;

/**
 * Builder pattern implementation ensuring preparation and delivery of Tenant instance.
 */
public class TenantBuilder {

    private Tenant tenant;
    private final String tenantLabel;
    private Identifier tenantId;
    private final Entity originPredecessorEvent;
    private final Boolean isActivityStatus;

    /**
     * Default constructor.
     *
     * @param label                  Mandatory label defining the name of the tenant to build.
     * @param originPredecessorEvent Mandatory predecessor event's identity (e.g command event identity).
     * @param isActivityStatus       Optional activity status regarding the tenant instance to build.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public TenantBuilder(String label, Entity originPredecessorEvent, Boolean isActivityStatus) throws IllegalArgumentException {
        if (label == null || label.isEmpty())
            throw new IllegalArgumentException("label parameter is required!");
        if (originPredecessorEvent == null)
            throw new IllegalArgumentException("originPredecessorEvent parameter is required!");

        this.tenantLabel = label;
        this.originPredecessorEvent = originPredecessorEvent;
        this.isActivityStatus = isActivityStatus;
    }

    public void buildInstance() throws Exception {
        prepareTenantId();
        createInstance();
        prepareTenantDescription();
    }

    /**
     * Prepare a tenant descriptor and assign to the tenant instance, including a default HistoryState.COMMITTED status.
     *
     * @throws ImmutabilityException When impossible read of tenant instance parent (predecessor identity).
     */
    private void prepareTenantDescription() throws ImmutabilityException {
        HashMap<String, Object> propertyCurrentValue = new HashMap<>();
        propertyCurrentValue.put(TenantDescriptor.PropertyAttributeKey.LABEL.name(), tenantLabel);
        TenantDescriptor ownerProperty = new TenantDescriptor(/* owner of description */tenant.parent(), propertyCurrentValue, /* status */HistoryState.COMMITTED);
        tenant.setLabel(ownerProperty);
    }

    /**
     * Create the instance of tenant
     */
    private void createInstance() {
        tenant = new Tenant(/* origin command event as predecessor */originPredecessorEvent, tenantId, isActivityStatus);
    }

    /**
     * Prepare builder of unique identified based on natural key (unique tenant naming)
     */
    private void prepareTenantId() throws Exception {
        // Like tenant description is mutable information, a tenant identify can't be based on a natural key relative to the original tenant label
        // So technical identifier shall be used for uui of tenant generation
        tenantId = IdentifierStringBased.generate(tenantLabel);
    }

    public Tenant getResult() {
        return tenant;
    }
}
