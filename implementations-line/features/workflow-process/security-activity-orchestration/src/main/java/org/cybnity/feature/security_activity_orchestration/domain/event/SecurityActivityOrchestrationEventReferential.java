package org.cybnity.feature.security_activity_orchestration.domain.event;

/**
 * Referential of event types supported by the security activity orchestration
 * feature.
 * 
 * @author olivier
 *
 */
public enum SecurityActivityOrchestrationEventReferential {

	/**
	 * Event regarding an established context and priorities for managing security
	 * and privacy risk.
	 */
	RiskAssessmentPrepared,
	/**
	 * Event regarding finished categorization of the system and the information
	 * processed, stored, and transmitted by it (based on an analysis of the impact
	 * of loss).
	 */
	InformationSystemCategorized,
	/**
	 * Event regarding finalized selection of an initial set of controls for the
	 * system and tailoring of the controls as needed to reduce risk to an
	 * acceptable level based on an assessment of risk.
	 */
	SecurityControlsSelected,
	/**
	 * Event regarding implemented controls and description how the controls are
	 * employed within the system and its environment of operation.
	 */
	SecurityControlsImplemented,
	/**
	 * Event regarding finalized assessment of the controls which determined if the
	 * controls are implemented correctly, operating as intended, and producing the
	 * desired outcomes with respect to satisfying the security and privacy
	 * requirements.
	 */
	SecurityControlsInstantiationAssessed,
	/**
	 * Event regarding finalized authorization of a system or common controls based
	 * on a determination that the risk to organizational operations and assets,
	 * individuals, other organizations, and the Nation is acceptable.
	 */
	SystemAuthorized,
	/**
	 * Event regarding a risk management element that was changed about finalized
	 * risk management individual identities definition
	 */
	RiskManagementExecutionIndividualsIdentified,
	/**
	 * Event regarding a risk management element that was changed about finalized
	 * risk management roles definition.
	 */
	RiskManagementExecutionIndividualsKeyRolesAssigned;

}
