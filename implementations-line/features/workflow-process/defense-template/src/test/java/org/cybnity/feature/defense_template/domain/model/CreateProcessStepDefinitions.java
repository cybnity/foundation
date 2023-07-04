package org.cybnity.feature.defense_template.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.LinkedHashSet;

import org.cybnity.feature.defense_template.domain.model.process.NISTRMFProcessBuilder;
import org.cybnity.feature.defense_template.domain.model.sample.writemodel.Organization;
import org.cybnity.feature.defense_template.domain.model.sample.writemodel.TestSampleFactory;
import org.cybnity.feature.security_activity_orchestration.IProcessBuilder;
import org.cybnity.feature.security_activity_orchestration.domain.model.Process;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Behavior test regarding the {@code ProcessBuildDirector} feature responsible
 * of process instance type creation. The tested feature is named
 * createProcess(...).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class CreateProcessStepDefinitions {

	private String referentialName, processName, description;
	private IProcessBuilder builder;
	private Process processModel;

	@Given("A {string} and {string} template name is selected as ready for reuse")
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
				builder = NISTRMFProcessBuilder.instance(processIdentifiers, processParent, processName);
			}
		} else if (referential.equalsIgnoreCase("ISO/IEC")) {
			if (processName.contains("27005")) {

			}
		} else {
			throw new PendingException("Unsupported referential value!");
		}
	}

	@When("I try to build a process instance respecting the template {string}")
	public void i_try_to_build_a_process_instance_respecting_the_template(String description) throws Exception {
		this.description = description;
		// Select a coordinator of build
		ProcessBuildDirector director = new ProcessBuildDirector(this.builder);
		// Delegate the build driven by the coordinator
		director.make();
		// Get the built instance as specialized process type
		this.processModel = this.builder.getResult();
	}

	@Then("I get a standard {string} process instance deployed into the company")
	public void i_get_a_standard_process_instance_deployed_into_the_company(String processType) throws Exception {
		// Verify the created process' name
		assertEquals(this.processName, this.processModel.name());

	}

	@Then("{int} steps are defined as process staging")
	public void steps_are_defined_as_process_staging(int quantity) throws Exception {
		// Check that staging is defined
		//assertNotNull(this.processModel.staging(), "Staging shall have been defined by the builder!");
		// Verify the quantity of steps defined in the staging
		//assertEquals(quantity, this.processModel.staging().steps().size());
	}
}
