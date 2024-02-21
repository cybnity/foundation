package org.cybnity.feature.defense_template.service;

import org.cybnity.feature.defense_template.domain.model.sample.writemodel.Organization;
import org.cybnity.feature.security_activity_orchestration.domain.model.Process;
import org.cybnity.feature.security_activity_orchestration.domain.model.ProcessBuilder;
import org.cybnity.framework.domain.IdentifierStringBased;
import org.cybnity.framework.domain.model.DomainEntity;
import org.cybnity.framework.immutable.BaseConstants;
import org.cybnity.framework.immutable.Identifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProcessTemplateXMLParserUseCaseTest {

	private static final String FILENAME = "nist_rmf_process_template_en.xml";

	private DomainEntity companyIdentity;
	private Organization company;

	@BeforeEach
	public void init() {
		companyIdentity = new DomainEntity(
				new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), UUID.randomUUID().toString()));
		company = new Organization(companyIdentity.identified());
	}

	/**
	 * Test the read of a valid XML document specifying a process template (NIST RMF
	 * sample) by a parsing implementation that collect template contents into a
	 * process builder allowing it to build a process instance.
	 * 
	 * Check the built instance conformity.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenXMLTemplate_whenParsing_thenValueCollected() throws Exception {
		DomainEntity processIdentity = new DomainEntity(
				new IdentifierStringBased(BaseConstants.IDENTIFIER_ID.name(), UUID.randomUUID().toString()));
		LinkedHashSet<Identifier> processIds = new LinkedHashSet<>();
		processIds.add(processIdentity.identified());

		// Prepare the build required contents from a XML document specifying a process
		// template.
		XMLProcessProcessBuilder builder = XMLProcessProcessBuilder.instance(processIds, company);
		ProcessTemplateXMLParser p = new ProcessTemplateXMLParser();
		InputStream s = ClassLoader.getSystemClassLoader().getResourceAsStream(FILENAME);
		// Execute parsing of template resource
		p.parse(s, builder);

		// Execute the build of process instance
		builder.build();
		Process instance = builder.getResult();

		// Check the built instance
		assertNotNull(instance);
		ProcessBuilder.validateConformity(instance);
	}
}
