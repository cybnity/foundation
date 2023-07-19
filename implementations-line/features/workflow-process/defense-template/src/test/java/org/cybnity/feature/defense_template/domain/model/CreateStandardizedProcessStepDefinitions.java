package org.cybnity.feature.defense_template.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.cybnity.feature.defense_template.domain.model.sample.writemodel.Organization;
import org.cybnity.feature.defense_template.domain.model.sample.writemodel.TestSampleFactory;
import org.cybnity.feature.defense_template.service.XMLProcessProcessBuilder;
import org.cybnity.feature.security_activity_orchestration.IProcessBuilder;
import org.cybnity.feature.security_activity_orchestration.domain.model.Process;
import org.cybnity.feature.security_activity_orchestration.domain.model.Staging;
import org.cybnity.feature.security_activity_orchestration.domain.model.Step;
import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.domain.IState;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Behavior test regarding the {@code ProcessBuildDirector} feature responsible
 * of process instance type creation.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class CreateStandardizedProcessStepDefinitions {

	private String referentialName, processName, fullName;
	private IProcessBuilder builder;
	private Process processModel;
	private Locale i18NLanguage;

	private Map<Locale, String> config;

	/**
	 * Initialize the supported templates.
	 */
	private void initTemplatesContext() {
		config = new Hashtable<>();
		config.put(Locale.ENGLISH, "nist_rmf_process_template_en.xml");
		config.put(Locale.FRENCH, "nist_rmf_process_template_fr.xml");
	}

	@Given("A {string} and {string} template is selected as ready for reuse")
	public void a_and_template_name_is_selected_as_ready_for_reuse(String referential, String processName) {
		// Set customized names and contents of the process to build
		// as configuration
		this.referentialName = referential;
		this.processName = processName;

		// Define a sample of process identifier
		LinkedHashSet<Identifier> processIdentifiers = new LinkedHashSet<>();
		processIdentifiers.add(TestSampleFactory.createIdentity().identified());
		// Define a company sample
		Organization processParent = TestSampleFactory.createOrganization(TestSampleFactory.createIdentity());

		// Define the builder according to the standard requested
		if (referential.equalsIgnoreCase("nist")) {
			// Select the builder based on the requested process name
			if (processName.contains("RMF")) {
				builder = XMLProcessProcessBuilder.instance(processIdentifiers, processParent);
			}
		} else if (referential.equalsIgnoreCase("ISO/IEC")) {
			if (processName.contains("27005")) {

			}
		} else {
			throw new PendingException("Unsupported referential value!");
		}
	}

	@Given("{string} translation is selected to be apply on generated contents")
	public void translation_is_selected_to_be_apply_on_generated_contents(String language) {
		this.i18NLanguage = Locale.forLanguageTag(language);
	}

	@When("I try to build a process instance respecting the template {string}")
	public void i_try_to_build_a_process_instance_respecting_the_template(String fullName) throws Exception {
		this.fullName = fullName;
		// Select a coordinator of build
		ProcessBuildDirector director = new ProcessBuildDirector(this.builder);
		// initialize templates catalog
		initTemplatesContext();
		// Delegate the build driven by the coordinator
		InputStream s = ClassLoader.getSystemClassLoader().getResourceAsStream(config.get(this.i18NLanguage));
		director.make(s);
		// Get the built instance as specialized process type
		this.processModel = this.builder.getResult();
	}

	@Then("I get a standard {string} process instance deployed into the company")
	public void i_get_a_standard_process_instance_deployed_into_the_company(String processType) throws Exception {
		// Verify the created process' name
		assertEquals(this.processName, this.processModel.name());

		// Verify assigned standard to process instance
		assertFalse(this.processModel.description().properties().isEmpty());
		for (Attribute property : this.processModel.description().properties()) {
			if ("standard".equals(property.name())) {
				// Check that referential name of process is good
				assertEquals(this.referentialName, property.value());
			}
			if ("fullName".equals(property.name())) {
				// Check that full name of process is good
				assertTrue(this.fullName.equalsIgnoreCase(property.value()));
			}
			if ("description".equals(property.name())) {
				// Check that description of process is good
				assertNotNull(property.value());
			}
			if ("finalizedStateDescription".equals(property.name())) {
				// Check that description of process is good
				assertNotNull(property.value());
			}
		}
	}

	@And("{int} phases are defined as process staging that are ordered and named {string}")
	public void phases_are_defined_as_process_staging_that_are_ordered_and_named(int quantity, String phaseNames)
			throws Exception {
		Staging staging = this.processModel.staging();
		// Check that staging is defined
		assertNotNull(staging, "Staging shall have been defined by the builder!");
		// Verify the quantity of steps defined in the staging
		assertEquals(quantity, staging.steps().size());

		// Check that all phase names are defined
		String[] named = phaseNames.split(",");

		// Verify if steps have good names and order
		List<Step> steps = staging.steps();
		int index = 0;
		for (String aStageNamed : named) {
			// Search existing phase
			Step aStep = steps.get(index);
			// Check existing hoped steps
			assertNotNull(aStep, "Missing step definition that should have been built!");
			// Verify equals name
			assertEquals(aStageNamed, aStep.name(),
					"Invalid step name or position in the staging order which have been built!");
			// When existing sub-tasks, check their conformity
			if (aStep.subStates() != null && !aStep.subStates().isEmpty()) {
				for (IState step : aStep.subStates()) {
					assertTrue(Step.class.isAssignableFrom(step.getClass()), "invalid type of sub-task!");
					Step taskItem = (Step) step;
					// When same owner between staging phase and sub-tasks
					assertEquals(aStep.owner(), taskItem.owner(), "Invalid sub-task owner!");
				}
			}
			index++;
		}
	}

}
