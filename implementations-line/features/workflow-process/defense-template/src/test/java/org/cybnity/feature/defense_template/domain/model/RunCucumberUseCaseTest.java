package org.cybnity.feature.defense_template.domain.model;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("org/cybnity/feature/defense_template/domain/model")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.cybnity.feature.defense_template.domain.model")
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class RunCucumberUseCaseTest {

}
