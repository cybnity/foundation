package org.cybnity.feature.defense_template;

import org.cybnity.feature.defense_template.domain.model.ReferentialUseCaseTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Suite of all technical and behavior tests regarding the domain components
 * capabilities.
 * 
 * @author olivier
 *
 */
@Suite
@SelectClasses({ ReferentialUseCaseTest.class })
public class AllTests {
}