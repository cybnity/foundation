package org.cybnity.feature.defense_template.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;

import org.cybnity.framework.domain.Attribute;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * DOM parsing implementation class of XML document defining a process template
 * as specification reusable by a process builder to instantiate a customized
 * process (e.g NIST RMF process).
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class ProcessTemplateXMLParser {

	private DocumentBuilderFactory dbf;

	/**
	 * Name of node supported by the parser regarding the specification of a process
	 * defined into a valid XML document.
	 *
	 */
	public enum NodeNameSpecification {
		Process, Description, Activation, Completion, Properties, Attribute, Staging, Step, Name, SubStates,
		ActivationIncomingConditions;
	}

	/**
	 * Name of node attribute supported by the parser regarding the specification of
	 * a process defined into a valid XML document.
	 */
	public enum NodeAttributeNameSpecification {
		name, isActiveStatus, percentage, referential;
	}

	/**
	 * Default constructor.
	 * 
	 * @throws FactoryConfigurationError    in case of service configuration error
	 *                                      or if the implementation is not
	 *                                      available or cannot be instantiated.
	 * @throws ParserConfigurationException When the used DocumentBuilderFactory or
	 *                                      the DocumentBuilders it creates cannot
	 *                                      support a feature (e.g secure
	 *                                      processing) used by this constructor.
	 */
	public ProcessTemplateXMLParser() throws ParserConfigurationException, FactoryConfigurationError {
		// Create the factory
		dbf = DocumentBuilderFactory.newInstance();
		// Recommended to process XML securely, avoiding attacks like XML external
		// entities (XXE)
		dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
	}

	/**
	 * Perform the parsing of an XML document with automatic notification of
	 * observer collecting the values detected from the XML document elements.
	 * 
	 * @param documentIn Mandatory stream to read as XML document.
	 * @param observer   Mandatory observer that shall be notify during the parsing.
	 * @throws ParserConfigurationException       When any required configuration
	 *                                            for document building (DOM parsing
	 *                                            based) is missing.
	 * @throws IOException                        If any IO errors occur during the
	 *                                            parsing.
	 * @throws SAXException                       If any parse errors occur during
	 *                                            the parsing.
	 * @throws IllegalArgumentException           When required parameter is
	 *                                            missing.
	 * @throws NotSupportedTemplateValueException When a node element description is
	 *                                            not valid.
	 */
	public void parse(InputStream documentIn, IProcessBuildPreparation observer) throws ParserConfigurationException,
			IOException, SAXException, IllegalArgumentException, NotSupportedTemplateValueException {
		if (documentIn == null)
			throw new IllegalArgumentException("DocumentIn parameter is required!");
		if (observer == null)
			throw new IllegalArgumentException("Observer parameter is required!");
		// Start document parsing
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(documentIn);
		// Optional but recommended about normalization in DOM parsing with java
		doc.getDocumentElement().normalize();

		// Execute the search of each desired content representing a process
		readAllContents(doc, observer);
	}

	/**
	 * Execute the search of each contents defined by the template, and call each
	 * dedicated value listener.
	 * 
	 * @param doc      Mandatory document instance already parsed.
	 * @param observer Mandatory observer to notify.
	 * @throws NotSupportedTemplateValueException When referential event type
	 *                                            description is defined in node but
	 *                                            that is unknown from referential
	 *                                            indicated.
	 */
	private void readAllContents(Document doc, IProcessBuildPreparation observer)
			throws NotSupportedTemplateValueException {
		NodeList root = doc.getElementsByTagName(NodeNameSpecification.Process.name());
		Node process = root.item(0);
		if (process.getNodeType() == Node.ELEMENT_NODE) {
			getProcessName(process, observer);
			getDescription(process, observer);
			getProcessStates(process, observer);
			getProcessStaging(process, observer);
		}
	}

	/**
	 * Find steps.
	 * 
	 * @param parent Mandatory steps parent (e.g SubStates container node).
	 * @return A list of steps or empty list.
	 * @throws NotSupportedTemplateValueException When referential event type
	 *                                            description is defined in node but
	 *                                            that is unknown from referential
	 *                                            indicated.
	 */
	private List<StepSpecification> getSteps(Node parent) throws NotSupportedTemplateValueException {
		List<StepSpecification> steps = new LinkedList<>();
		if (parent != null && parent.hasChildNodes()) {
			// Find all steps defining the parent
			NodeList stepItems = parent.getChildNodes();
			Node aStepItem;
			Element stepElement, stepSpecElement;
			Collection<Attribute> stepAttr;
			NodeList stepDefItems;
			Node aStepSpecItem;
			String stepName;
			StepSpecification step;
			List<StepSpecification> subStates;
			for (int i = 0; i < stepItems.getLength(); i++) {
				aStepItem = stepItems.item(i);
				if (aStepItem != null && aStepItem.getNodeType() == Node.ELEMENT_NODE
						&& aStepItem.getNodeName().equals(NodeNameSpecification.Step.name())) {
					// Read the specification contents defining a step
					stepElement = (Element) aStepItem;
					// Prepare step specification to retain
					step = new StepSpecification();

					// Read each step definition element
					stepDefItems = stepElement.getChildNodes();
					for (int t = 0; t < stepDefItems.getLength(); t++) {
						aStepSpecItem = stepDefItems.item(t);

						if (aStepSpecItem != null && aStepSpecItem.getNodeType() == Node.ELEMENT_NODE) {
							stepSpecElement = (Element) aStepSpecItem;

							// Identify each type of step specification element to read
							// and prepare specification container
							if (stepSpecElement.getNodeName().equals(NodeNameSpecification.Name.name())) {
								// Read mandatory step name
								stepName = stepSpecElement.getTextContent();
								if (stepName == null || "".equals(stepName))
									throw new NotSupportedTemplateValueException(
											"Invalid Step node specificatin found without mandatory text content value!");
								step.setName(stepName);
							}

							if (stepSpecElement.getNodeName().equals(NodeNameSpecification.Properties.name())) {
								// Read optional Properties and attributes
								// Find Properties' attributes
								stepAttr = getAttributes(stepSpecElement);
								if (stepAttr != null && !stepAttr.isEmpty()) {
									step.setProperties(stepAttr);
								}
							}

							if (stepSpecElement.getNodeName().equals(NodeNameSpecification.Activation.name())) {
								// Read optional Activation status
								String isActive = stepSpecElement
										.getAttribute(NodeAttributeNameSpecification.isActiveStatus.name());
								if (!"".equals(isActive)) {
									step.setActivationState(Boolean.getBoolean(isActive));
								}
								if (stepSpecElement.hasChildNodes()) {
									// Read optional defined activation incoming conditions
									NodeList conditions = stepSpecElement.getChildNodes();
									Node activationCondition;
									Collection<Enum<?>> activationEventTypes = new ArrayList<>();
									String activationEventType, eventTypeReferential;
									Node eventType;
									Element activationIncomingEventTypeElement;
									NodeList incomingEventTypes;
									for (int c = 0; c < conditions.getLength(); c++) {
										activationCondition = conditions.item(c);
										if (activationCondition != null && activationCondition.getNodeName()
												.equals(NodeNameSpecification.ActivationIncomingConditions.name())) {
											incomingEventTypes = activationCondition.getChildNodes();

											for (int incomingEventTypesCount = 0; incomingEventTypesCount < incomingEventTypes
													.getLength(); incomingEventTypesCount++) {
												// Identify the logical names of each event type supported as cause of
												// automatic activation of the step
												eventType = incomingEventTypes.item(incomingEventTypesCount);
												if (eventType.getNodeType() == Node.ELEMENT_NODE) {
													activationIncomingEventTypeElement = (Element) eventType;
													activationEventType = activationIncomingEventTypeElement
															.getNodeName();
													eventTypeReferential = activationIncomingEventTypeElement
															.getAttribute(
																	NodeAttributeNameSpecification.referential.name());
													if (!"".equals(eventTypeReferential)) {
														try {
															// Instantiate event type referential supporting the type of
															// event type specified in template
															Object[] referentialEnum = getClass().getClassLoader()
																	.loadClass(eventTypeReferential).getEnumConstants();

															if (referentialEnum == null)
																throw new NotSupportedTemplateValueException(
																		"Step specification contain an event type declaration (sub-node of ActivationIncomingConditions) about an invalid referential attribute value (perhaps is not an Enum type) that is unknown from the class path!");
															Enum<?> refEnum;
															// Instantiate event type from supported referential
															for (Object referentialCustomEventType : referentialEnum) {
																if (referentialCustomEventType.getClass().isEnum()) {
																	refEnum = (Enum<?>) referentialCustomEventType;
																	// Detect the supported event type as defined in the
																	// template
																	if (refEnum.name()
																			.equalsIgnoreCase(activationEventType)) {
																		// Supported event type instance
																		// Save it as cause of step activation
																		activationEventTypes.add(refEnum);
																		break; // Stop search from referential
																	}
																}
															}
														} catch (ClassNotFoundException cnfe) {
															// Unknown type of enumeration referential class from class
															// path
															throw new NotSupportedTemplateValueException(cnfe);
														}
													} else {
														throw new NotSupportedTemplateValueException(
																"One Step with ActivationIncomingConditions sub-node require mandatory referential attribute which is currently not defined!");
													}
												}
											}
										}
									}

									// Define the found specified step activation conditions into the step result
									if (!activationEventTypes.isEmpty())
										step.setActivationEventTypes(activationEventTypes);
								}

							}

							if (stepSpecElement.getNodeName().equals(NodeNameSpecification.Completion.name())) {
								// Read status
								String name = stepSpecElement.getAttribute(NodeAttributeNameSpecification.name.name());
								String percent = stepSpecElement
										.getAttribute(NodeAttributeNameSpecification.percentage.name());
								if (!"".equals(name)) {
									step.setCompletionName(name);
								}
								if (!"".equals(percent)) {
									step.setCurrentPercentageOfCompletion(Float.valueOf(percent));
								}
							}

							if (stepSpecElement.getNodeName().equals(NodeNameSpecification.SubStates.name())) {
								// Read optional existing SubStates (recursive call for sub-steps read)
								subStates = getSteps(stepSpecElement);
								if (!subStates.isEmpty()) {
									step.setSubStates(subStates);
								}
							}
						}

					}

					// Save step specification in results container
					steps.add(step);
				}
			}
		}
		return steps;
	}

	/**
	 * Find process staging (main phases) and optional sub-tasks elements.
	 * 
	 * @param parent   Mandatory process to read.
	 * @param observer To notify.
	 * @throws NotSupportedTemplateValueException When referential event type
	 *                                            description is defined in node but
	 *                                            that is unknown from referential
	 *                                            indicated.
	 */
	private void getProcessStaging(Node parent, IProcessBuildPreparation observer)
			throws NotSupportedTemplateValueException {
		if (observer != null && parent != null && parent.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) parent;
			NodeList staging = element.getElementsByTagName(NodeNameSpecification.Staging.name());
			// Select the unique staging normally defined
			Node stagingNode = staging.item(0);
			List<StepSpecification> stagingPhases = getSteps(stagingNode);
			if (!stagingPhases.isEmpty()) {
				// Notify about staging steps
				observer.processStaging(stagingPhases);
			}
		}
	}

	/**
	 * Find process activation and completion state elements.
	 * 
	 * @param parent   Mandatory process to read.
	 * @param observer To notify.
	 */
	private void getProcessStates(Node parent, IProcessBuildPreparation observer) {
		if (observer != null && parent != null && parent.getNodeType() == Node.ELEMENT_NODE) {
			// Find activation element
			NodeList processNodes = parent.getChildNodes();
			if (processNodes != null) {
				Node aProcessItem;
				Element anElement;
				for (int i = 0; i < processNodes.getLength(); i++) {
					// Identify the type of each activation content
					aProcessItem = processNodes.item(i);
					if (aProcessItem != null) {
						if (aProcessItem.getNodeType() == Node.ELEMENT_NODE) {
							anElement = (Element) aProcessItem;
							// Find Activation element
							if (anElement.getNodeName().equals(NodeNameSpecification.Activation.name())) {
								// Read status
								String isActive = anElement
										.getAttribute(NodeAttributeNameSpecification.isActiveStatus.name());
								if (!"".equals(isActive)) {
									// Notify about activation state
									observer.processActivation(Boolean.getBoolean(isActive));
								}
							}
							// Find Completion element
							if (anElement.getNodeName().equals(NodeNameSpecification.Completion.name())) {
								// Read status
								String name = anElement.getAttribute(NodeAttributeNameSpecification.name.name());
								String percent = anElement
										.getAttribute(NodeAttributeNameSpecification.percentage.name());
								if (!"".equals(name)) {
									// Notify about completion state
									observer.processCompletion(name,
											(!"".equals(percent)) ? Float.valueOf(percent) : null);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Find process description element.
	 * 
	 * @param parent   Mandatory process to read.
	 * @param observer To notify.
	 */
	private void getDescription(Node parent, IProcessBuildPreparation observer) {
		if (observer != null && parent.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) parent;
			NodeList description = element.getElementsByTagName(NodeNameSpecification.Description.name());
			// Select the unique description normally defined
			Node descriptionNode = description.item(0);
			if (descriptionNode != null && descriptionNode.hasChildNodes()) {
				// Load the description items
				NodeList descriptionItems = descriptionNode.getChildNodes();
				Node aDescriptionItem;
				Element descriptionElement;
				Collection<Attribute> descAttr;
				for (int i = 0; i < descriptionItems.getLength(); i++) {
					// Identify the type of each description content
					aDescriptionItem = descriptionItems.item(i);
					if (aDescriptionItem != null) {
						if (aDescriptionItem.getNodeType() == Node.ELEMENT_NODE) {
							descriptionElement = (Element) aDescriptionItem;
							// Find Properties element
							if (descriptionElement.getNodeName().equals(NodeNameSpecification.Properties.name())) {
								// Find Properties' attributes
								descAttr = getAttributes(descriptionElement);
								if (descAttr != null && !descAttr.isEmpty()) {
									// Notify observer about prepared values
									observer.processDescriptionProperties(descAttr);
								}
							}

						}
					}
				}
			}
		}
	}

	/**
	 * Find all attribute elements hosted by a parent node.
	 * 
	 * @param attributesParent Mandatory parent node to read.
	 * @return A list of found Attribute or empty collection.
	 */
	private Collection<Attribute> getAttributes(Node attributesParent) {
		Collection<Attribute> result = new ArrayList<>();
		if (attributesParent != null) {
			NodeList propertiesDef = attributesParent.getChildNodes();
			if (propertiesDef != null) {
				// Read all the Attribute elements defined as Properties
				Node attrNode;
				Element propertyAttribute;
				String at, content;
				for (int i = 0; i < propertiesDef.getLength(); i++) {
					attrNode = propertiesDef.item(i);
					if (attrNode != null && attrNode.getNodeName().equals(NodeNameSpecification.Attribute.name())) {
						if (attrNode.getNodeType() == Node.ELEMENT_NODE) {
							propertyAttribute = (Element) attrNode;
							at = propertyAttribute.getAttribute(NodeAttributeNameSpecification.name.name());
							content = propertyAttribute.getTextContent();
							if (!"".equals(at) && !"".equals(content))
								// Prepare attribute instance
								result.add(new Attribute(
										propertyAttribute.getAttribute(NodeAttributeNameSpecification.name.name()),
										content));
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Find the name of the process.
	 * 
	 * @param parent   Mandatory process to read.
	 * @param observer To notify about detected values.
	 */
	private void getProcessName(Node parent, IProcessBuildPreparation observer) {
		if (observer != null && parent.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) parent;
			// Read name from unique found process element
			String name = element.getAttribute(NodeAttributeNameSpecification.name.name());
			if (!"".equals(name))
				observer.processNamedAs(name);
		}
	}
}
