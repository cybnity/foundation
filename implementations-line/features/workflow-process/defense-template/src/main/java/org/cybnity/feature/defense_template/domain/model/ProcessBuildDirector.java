package org.cybnity.feature.defense_template.domain.model;

import org.cybnity.feature.defense_template.service.IProcessBuildPreparation;
import org.cybnity.feature.defense_template.service.NotSupportedTemplateValueException;
import org.cybnity.feature.defense_template.service.ProcessTemplateXMLParser;
import org.cybnity.feature.security_activity_orchestration.IProcessBuilder;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Responsible of coordination regarding the build of several types of
 * processes.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class ProcessBuildDirector {

	/**
	 * Build processor managed by this director.
	 */
	private IProcessBuilder builder;

	/**
	 * Default constructor.
	 * 
	 * @param builder Mandatory builder to manage for process instance build.
	 * @throws IllegalArgumentException When builder parameter is undefined.
	 */
	public ProcessBuildDirector(IProcessBuilder builder) throws IllegalArgumentException {
		// Initialize the default builder
		change(builder);
	}

	/**
	 * Change the builder that director shall use for process instance build.
	 * 
	 * @param builder Mandatory builder instance.
	 * @throws IllegalArgumentException When mandatory parameter is missing.
	 */
	public void change(IProcessBuilder builder) throws IllegalArgumentException {
		if (builder == null)
			throw new IllegalArgumentException("Builder parameter is required!");
		this.builder = builder;
	}

	/**
	 * Manage the life cycle of instance creation including the preparation phase
	 * (e.g contents reed from XML document template) and build phase of process
	 * instance.
	 * 
	 * @param templateDocument Optional template to be used during the contents
	 *                         preparation phase. If null, preparation phase is
	 *                         ignored and default build is executed without
	 *                         contents preparation.
	 * @throws ImmutabilityException              When impossible
	 * @throws ParserConfigurationException       When error occurred during build
	 *                                            preparation step using template
	 *                                            document.
	 * @throws SAXException                       When template parsing problem.
	 * @throws IOException                        When template input stream read
	 *                                            error.
	 * @throws NotSupportedTemplateValueException When a template element is not
	 *                                            valid.
	 */
	public void make(InputStream templateDocument) throws ImmutabilityException, ParserConfigurationException,
			SAXException, IOException, NotSupportedTemplateValueException {
		if (templateDocument != null && IProcessBuildPreparation.class.isAssignableFrom(this.builder.getClass())) {
			// Prepare the build required contents from a XML document specifying a process
			// template into a language supported
			ProcessTemplateXMLParser p = new ProcessTemplateXMLParser();
			// Execute parsing of template resource with automatic setup of builder
			// prerequisite contents
			p.parse(templateDocument, (IProcessBuildPreparation) this.builder);
		}

		// Execute the unique or multiple steps of build managed by the builder
		this.builder.build();
	}
}
