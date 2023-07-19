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
	 * 
	 * The outcomes are:
	 * 
	 * - key risk management roles identified;
	 * 
	 * - organizational risk management strategy established, risk tolerance
	 * determined;
	 * 
	 * - organization-wide risk assessment;
	 * 
	 * - organization-wide strategy for continuous monitoring developed and
	 * implemented;
	 * 
	 * - common controls identified.
	 */
	RiskAssessmentPrepared,
	/**
	 * Event regarding finished categorization of the system and the information
	 * processed, stored, and transmitted by it (based on an analysis of the impact
	 * of loss).
	 * 
	 * The outcomes are:
	 * 
	 * - system characteristics documented;
	 * 
	 * - security categorization of the system and information completed;
	 * 
	 * - categorization decision reviewed/approved by authorizing official.
	 */
	InformationSystemCategorized,
	/**
	 * Event regarding finalized selection of an initial set of controls for the
	 * system and tailoring of the controls as needed to reduce risk to an
	 * acceptable level based on an assessment of risk.
	 * 
	 * The outcomes are:
	 * 
	 * - control baselines selected and tailored;
	 * 
	 * - controls designated as system-specific, hybrid, or common;
	 * 
	 * - controls allocated to specific system components;
	 * 
	 * - system-level continuous monitoring strategy developed;
	 * 
	 * - security and privacy plans that reflect the control selection, designation,
	 * and allocation are reviewed and approved.
	 */
	SecurityControlsSelected,
	/**
	 * Event regarding implemented controls and description how the controls are
	 * employed within the system and its environment of operation.
	 * 
	 * The outcomes are:
	 * 
	 * - controls specified in security and privacy plans implemented;
	 * 
	 * - security and privacy plans updated to reflect controls as implemented.
	 */
	SecurityControlsImplemented,
	/**
	 * Event regarding finalized assessment of the controls which determined if the
	 * controls are implemented correctly, operating as intended, and producing the
	 * desired outcomes with respect to satisfying the security and privacy
	 * requirements.
	 * 
	 * The outcomes are:
	 * 
	 * - assessor/assessment team selected;
	 * 
	 * - security and privacy assessment plans developed;
	 * 
	 * - assessment plans are reviewed and approved;
	 * 
	 * - control assessments conducted in accordance with assessment plans;
	 * 
	 * - security and privacy assessment reports developed;
	 * 
	 * - remediation actions to address deficiencies in controls are taken;
	 * 
	 * - security and privacy plans are updated to reflect control implementation
	 * changes based on assessments and remediation actions;
	 * 
	 * - plan of action and milestones developed.
	 */
	SecurityControlsInstantiationAssessed,
	/**
	 * Event regarding finalized authorization of a system or common controls based
	 * on a determination that the risk to organizational operations and assets,
	 * individuals, other organizations, and the Nation is acceptable.
	 * 
	 * The outcomes are:
	 * 
	 * - authorization package (executive summary, system security and privacy plan,
	 * assessment report(s), plan of action and milestones);
	 * 
	 * - risk determination rendered;
	 * 
	 * - risk responses provided;
	 * 
	 * - authorization for the system or common controls is approved or denied.
	 */
	SystemAuthorized,
	/**
	 * Event regarding monitored system and its associated controls, on an ongoing
	 * basis to include assessing control effectiveness, documenting changes to the
	 * system and environment of operation, conducting risk assessments and impact
	 * analysis, and reporting the security and privacy posture of the system.
	 * 
	 * The outcomes are:
	 * 
	 * - system and environment of operation monitored in accordance with continuous
	 * monitoring strategy;
	 * 
	 * - ongoing assessments of control effectiveness conducted in accordance with
	 * continuous monitoring strategy;
	 * 
	 * - output of continuous monitoring activities analyzed and responded to
	 * process in place to report security and privacy posture to management;
	 * 
	 * - ongoing authorization conducted using results of continuous monitoring
	 * activities.
	 */
	SystemAndAssociatedControlsMonitored,
	/**
	 * Event regarding a risk management element that was changed about finalized
	 * risk management individual identities definition.
	 */
	RiskManagementExecutionIndividualsIdentified,
	/**
	 * Event regarding a risk management element that was changed about finalized
	 * risk management roles definition. It's an outcome event confirming that key
	 * risk management roles are identified.
	 */
	RiskManagementExecutionIndividualsKeyRolesAssigned,
	/**
	 * Event regarding a risk management strategy establishment finalized.
	 */
	RiskManagementStrategyEstablished,
	/**
	 * Event regarding prepared and/or updated risk assessment organization and/or
	 * system level risk assessment finalized.
	 */
	RiskAssessmentUpdated,
	/**
	 * Event regarding finalized risk assessment organization.
	 */
	OrganizationWideRiskAssessmentCompleted,
	/**
	 * Event regarding cyber security profiles establishment that is finalized.
	 */
	CybersecurityProfilesEstablished,
	/**
	 * Event regarding an organizationally tailored control baselines establishment
	 * that have been finalized.
	 */
	OrganizationallyTailoredControlBaselinesEstablished,
	/**
	 * Event regarding a finalized identification of common controls.
	 */
	CommonControlsIdentified,
	/**
	 * Event regarding documentation realized about common controls.
	 */
	CommonControlsDocumented,
	/**
	 * Event regarding performed publishing about common controls that are
	 * identified and documented.
	 */
	CommonControlsPublished,
	/**
	 * Event regarding finalized impact level prioritization.
	 */
	OrganizationSystemsPrioritizationConducted,
	/**
	 * Event regarding a continuous monitoring strategy that have been organized.
	 */
	ContinuousMonitoringStrategyOrganised,
	/**
	 * Event regarding a system mission or business supported processes
	 * identification that have been finalized.
	 */
	SystemMissionOrBusinessSupportedProcessesIdentified,
	/**
	 * Event regarding a finalized system stakeholders identification.
	 */
	SystemStakeholdersIdentified,
	/**
	 * Event regarding a finalized assets identification.
	 */
	AssetsIdentifiedAndPrioritized,
	/**
	 * Event a finalized authorization boundary determination.
	 */
	AuthorizationBoundaryDetermined,
	/**
	 * Event regarding a system information types identification that have been
	 * finalized.
	 */
	SystemInformationTypesIdentified,
	/**
	 * Event regarding information life cycle stages identification that have been
	 * finalized.
	 */
	InformationLifeCycleStagesIdentified,
	/**
	 * Event regarding a system level risk assessment that have be completed.
	 */
	SystemLevelRiskAssessmentCompleted,
	/**
	 * Event regarding a system security requirements definition that have been
	 * finalized.
	 */
	SecurityRequirementsDefined,
	/**
	 * Event regarding a system placement with enterprise architecture determination
	 * that have been finalized.
	 */
	SystemPlacementWithEnterpriseArchitectureDetermined,
	/**
	 * Event regarding security requirements allocation that have been finalized
	 * about system environment.
	 */
	EnvironmentSecurityRequirementsAllocated,
	/**
	 * Event regarding security requirements allocation that have been finalized
	 * about system.
	 */
	SystemSecurityRequirementsAllocated,
	/**
	 * Event regarding finalized system registration for purposes.
	 */
	ForPurposesSystemRegistered,
	/**
	 * Event regarding a system description finalized.
	 */
	SystemCharacteristicsDocumented,
	/**
	 * Event regarding a system security categorization finalized.
	 */
	SystemSecurityCategorizationCompleted,
	/**
	 * Event regarding a documentation finalized about the results of a system
	 * security categorization which was completed.
	 */
	SystemSecurityCategorizationResultsDocumented,
	/**
	 * Event regarding a system categorization results that have be analyzed and
	 * reviewed.
	 */
	SystemCategorizationResultsReviewed,
	/**
	 * Event regarding a system categorization decision that have been decided and
	 * that was approved.
	 */
	SystemCategorizationDecisionApproved,
	/**
	 * Event regarding a control baselines selection that have been finalized.
	 */
	ControlBaselinesSelected,
	/**
	 * Event regarding a control baselines tailoring that have been finalized.
	 */
	ControlBaselinesTailored,
	/**
	 * Event regarding security controls designation that have been finalized.
	 */
	SecurityControlsDesignated,
	/**
	 * Event regarding security controls system components allocation that have been
	 * finalized.
	 */
	SecurityControlsSystemComponentsAllocated,
	/**
	 * Event regarding control implementations plan documentation that have been
	 * finalized.
	 */
	SecurityControlsTailoringActionsDocumented,
	/**
	 * Event regarding system continuous monitoring strategy development that have
	 * been finalized.
	 */
	SystemContinuousMonitoringStrategyDeveloped,
	/**
	 * Event regarding system security plans review that have been finalized.
	 */
	SystemSecurityPlansReviewed,
	/**
	 * Event regarding system security plans decision that have been finalized.
	 */
	SystemSecurityPlansApproved,
	/**
	 * Event regarding security controls implementation plans that have been
	 * changed.
	 */
	SystemSecurityPlansUpdated,
	/**
	 * Event regarding assessor team selection that have been finalized.
	 */
	SecurityControlsAssessmentTeamSelected,
	/**
	 * Event regarding security controls instance assessment that was performed and
	 * that delivered a security assessment plans documentation.
	 */
	SecurityAssessmentPlansDocumentationProvided,
	/**
	 * Event regarding security controls instance assessment that was performed and
	 * that made review of security assessment plans documentation.
	 */
	SecurityAssessmentPlansReviewed,
	/**
	 * Event regarding security controls instance assessment that was performed
	 * where a approval of plans have been confirmed.
	 */
	SecurityAssessmentPlansApproved,
	/**
	 * Event regarding security controls assessment finalized.
	 */
	SecurityControlAssessmentsConducted,
	/**
	 * Event regarding security controls assessment reporting finalized.
	 */
	SecurityControlAssessmentReportsDeveloped,
	/**
	 * Event regarding security control deficiencies remediation that have been
	 * finalized.
	 */
	SecurityControlDeficienciesRemediationActionsTaken,
	/**
	 * Event regarding security plan change management finalized.
	 */
	SecurityPlanChangesUpdated,
	/**
	 * Event regarding a system authorization package development finalized.
	 */
	SystemAuthorizationPackaged,
	/**
	 * Event regarding a system risk determination rendering that have been
	 * finalized.
	 */
	SystemRiskDeterminationRendered,
	/**
	 * Event regarding finalized delivery of prepared risk responses.
	 */
	RiskResponsesProvided,
	/**
	 * Event regarding system security controls authorization decision that have
	 * been taken and approved.
	 */
	SystemSecurityControlsAuthorizationApproved,
	/**
	 * Event regarding system security controls authorization decision that have
	 * been taken and rejected.
	 */
	SystemSecurityControlsAuthorizationDenied,
	/**
	 * Event regarding system authorization decision that have been taken and
	 * approved.
	 */
	SystemAuthorizationApproved,
	/**
	 * Event regarding system authorization decision that have been taken and
	 * rejected.
	 */
	SystemAuthorizationDenied,
	/**
	 * Event regarding authorization report providing that have been finalized.
	 */
	SystemAuthorizationReported,
	/**
	 * Event regarding system and environment changes monitoring that have been
	 * deployed and is operational.
	 */
	SystemAndEnvironmentMonitored,
	/**
	 * Event regarding control effectiveness assessments that have been performed.
	 */
	SecurityControlsEffectivenessAssessmentsConducted,
	/**
	 * Event regarding analysis of monitored activities that have been finalized.
	 */
	SystemContinuousMonitoringActivitiesAnalyzed,
	/**
	 * Event regarding deployment of identified risk responses that have been
	 * finalized.
	 */
	SystemMonitoringActivitiesRiskResponded,
	/**
	 * Event regarding authorization package updates that have been finalized.
	 */
	RiskManagementDocumentsUpdated,
	/**
	 * Event regarding system security posture reporting that have been finalized.
	 */
	SecurityAndPrivacyPostureReported,
	/**
	 * Event regarding system authorization changes notification that have been
	 * finalized.
	 */
	RiskDeterminationAndAcceptanceDecisionCommunicated,
	/**
	 * Event regarding system disposal strategy definition that have been finalized.
	 */
	SystemDisposalStrategyDeveloped,
	/**
	 * Event regarding system disposal strategy implementation that have been
	 * finalized.
	 */
	SystemDisposalStrategyImplemented;

}
