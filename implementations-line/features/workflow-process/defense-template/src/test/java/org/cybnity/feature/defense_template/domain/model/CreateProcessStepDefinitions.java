package org.cybnity.feature.defense_template.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.cybnity.feature.defense_template.domain.model.ProcessBuildDirector;
import org.cybnity.feature.security_activity_orchestration.IProcessBuilder;
import org.cybnity.feature.security_activity_orchestration.domain.model.Process;

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
public class CreateProcessStepDefinitions {

	private String referentialName, processName, description;
	private IProcessBuilder builder;

	@Given("A {string} and {string} template name is selected as ready for reuse")
	public void a_and_template_name_is_selected_as_ready_for_reuse(String referential, String processName) {
		// Set customized names and contents of the process to build
		// as configuration
		this.referentialName = referential;
		this.processName = processName;
	}

	@When("I try to build a process instance respecting the template {string}")
	public void i_try_to_build_a_process_instance_respecting_the_template(String description) throws Exception {
		this.description = description;
		ProcessBuildDirector director = new ProcessBuildDirector();
		director.createProcess(builder);
	}

	@Then("I get a standard {string} process instance deployed into the company")
	public void i_get_a_standard_process_instance_deployed_into_the_company(String processType) throws Exception {
		// return the specialized class type of NISTRMF
		//Process process = builder.build();
		// Verify created process' name
		//assertEquals(processName, process.name());
	}
}
