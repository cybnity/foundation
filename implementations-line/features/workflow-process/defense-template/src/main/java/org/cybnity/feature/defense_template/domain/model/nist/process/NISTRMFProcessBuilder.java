package org.cybnity.feature.defense_template.domain.model.nist.process;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.cybnity.feature.security_activity_orchestration.domain.model.Process;
import org.cybnity.feature.security_activity_orchestration.domain.model.ProcessBuilder;
import org.cybnity.feature.security_activity_orchestration.domain.model.Staging;
import org.cybnity.feature.security_activity_orchestration.domain.model.Staging.PropertyAttributeKey;
import org.cybnity.feature.security_activity_orchestration.domain.model.Step;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.EntityReference;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Builder implementation class creating NIST RMF process instance that is
 * customized (as template) according to the NIST RMF standard.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class NISTRMFProcessBuilder extends ProcessBuilder {

	/**
	 * Translated value of contents regarding the NIST standard that support the
	 * template translated values.
	 */
	public static String I18N_BASE_NAME = "i18n_nist_templates";

	/**
	 * Default Constructor of process from unique or multiple identifiers to use for
	 * the process identity.
	 * 
	 * @param processIdentifiers Mandatory identity of the process to build.
	 * @param processParent      Mandatory predecessor of the process to build.
	 * @param processName        Mandatory name of the process to build.
	 * @param language           Mandatory language of translation to use for
	 *                           content build.
	 * @throws IllegalArgumentException When missing mandatory parameter.
	 */
	private NISTRMFProcessBuilder(LinkedHashSet<Identifier> processIdentifiers, Entity processParent,
			String processName, Locale language) throws IllegalArgumentException {
		super(processIdentifiers, processParent, processName, language, /*
																		 * static link to resource file including this
																		 * builder content translated contents
																		 */ I18N_BASE_NAME);
		if (language == null)
			throw new IllegalArgumentException("Language parameter is required!");
		this.i18nTranslation = language;
	}

	/**
	 * Get a builder instance allowing preparation of a process instantiation.
	 * 
	 * @param processIdentity Mandatory identity of the process to build.
	 * @param processParent   Mandatory predecessor of the process to build.
	 * @param processName     Mandatory name of the process to build.
	 * @param language        Mandatory language of translation to use for content
	 *                        build.
	 * @throws IllegalArgumentException When missing mandatory parameter.
	 */
	public static ProcessBuilder instance(LinkedHashSet<Identifier> processIdentifiers, Entity processParent,
			String processName, Locale language) throws IllegalArgumentException {
		return new NISTRMFProcessBuilder(processIdentifiers, processParent, processName, language);
	}

	/**
	 * Creation of the process instance according to the attributes defined into
	 * this builder component.
	 * 
	 * @throws ImmutabilityException    When impossible use of immutable version of
	 *                                  a build content.
	 * @throws IllegalArgumentException when mandatory content is missing to execute
	 *                                  the build process and shall be completed
	 *                                  before to call this method.
	 */
	public void build() throws ImmutabilityException, IllegalArgumentException {
		// Create standard basis instance of process
		super.build();

		// Get the built instance to be customized
		Process instance = super.getResult();

		// Customize the instance regarding its structure (e.g step)
		defineStaging(instance);
		// Prepare the process commands handlers behavior (e.g
		// states machine and change events promoted)
		defineCommandsHandling(instance);

		// Update the built instance as ready to return
		setResult(instance);
	}

	/**
	 * NIST RMF main phases defining its staging structure.
	 */
	private static RMFPropertyKeyI18n[] stagingPhases = { RMFPropertyKeyI18n.PROCESS_RMF_STAGING_STEP_PREPARE,
			RMFPropertyKeyI18n.PROCESS_RMF_STAGING_STEP_CATEGORIZE, RMFPropertyKeyI18n.PROCESS_RMF_STAGING_STEP_SELECT,
			RMFPropertyKeyI18n.PROCESS_RMF_STAGING_STEP_IMPLEMENT, RMFPropertyKeyI18n.PROCESS_RMF_STAGING_STEP_ASSESS,
			RMFPropertyKeyI18n.PROCESS_RMF_STAGING_STEP_AUTHORIZE, RMFPropertyKeyI18n.PROCESS_RMF_STAGING_STEP_MONITOR };

	/**
	 * Create and set the process with the RMF standard steps defining its staging.
	 * 
	 * @param instance To update.
	 * @return The updated instance referencing the process as owner.
	 * @throws ImmutabilityException    When problem of instantiation regarding
	 *                                  process instance update.
	 * @throws MissingResourceException When bundle of value translation is not
	 *                                  found, or missing required value is
	 *                                  detected.
	 */
	private void defineStaging(Process instance) throws ImmutabilityException, MissingResourceException {
		// Create the staging including the RMF standard steps
		List<Step> steps = new LinkedList<>();
		HashMap<String, Object> propertyCurrentValue = new HashMap<>();
		// Who is owner of properties to create?
		EntityReference propertyOwner = instance.root();

		// Read optional defined translation set of value
		ResourceBundle bundle = getI18NProperties();

		// Create each staging step without handling including sub-steps
		for (RMFPropertyKeyI18n aStepName : stagingPhases) {
			steps.add(new Step(propertyOwner, bundle.getString(aStepName.key()),
					/*
					 * processor allowing the step to interpret the commands handling relative to
					 * the step and/or to its subtasks
					 */ null));
		}
		propertyCurrentValue.put(PropertyAttributeKey.Steps.name(), steps);

		// Update the process with the staging
		instance.changeStaging(new Staging(propertyOwner.getEntity(), propertyCurrentValue, HistoryState.COMMITTED));
	}

	/**
	 * Create and set the handling service which manage the supported commands (e.g
	 * process attributes change) and handling service.
	 * 
	 * @param instance To update.
	 * @return The updated instance.
	 */
	private void defineCommandsHandling(Process instance) {
		// TODO handledCommandTypeVersions ()

		// TODO handle(Command change, IContext ctx)
		// Utiliser un delegate de type CommandHandlingService pour cet aggregate et le
		// setter

	}
}
