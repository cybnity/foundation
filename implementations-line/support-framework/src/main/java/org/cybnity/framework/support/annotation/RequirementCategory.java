package org.cybnity.framework.support.annotation;

/**
 * Type of CYBNITY requirement which can be linked as a design constraint or
 * implementation response.
 */
public enum RequirementCategory {
	/** Architecture concept, design rule, and/or requirement */
	Architecture("REQ_ARC"),
	/**
	 * Several values of a subject (e.g data, information, system) in different
	 * locations are identical (e.g data values are same for all instances of an
	 * application)
	 */
	Consistency("REQ_CONS"),
	/**
	 * Correct defects or cause; repair/replace faulty components without having to
	 * replace still working parts; prevent unexpected working conditions; maximize
	 * product’s useful life; maximize efficiency, reliability and safety; meet new
	 * requirements; easy future maintenance or changing environment
	 */
	Maintainability("REQ_MAIN"),
	/**
	 * Measure how well internal states of a system can be inferred from knowledge
	 * of its external outputs
	 */
	Observability("REQ_OBS"),
	/** Complete assigned tasks within a given time */
	Responsiveness("REQ_RESP"),
	/** Cope with errors during execution and cope with erroneous input */
	Robusteness("REQ_ROB"),
	/**
	 * Handle a growing amount of work by adding resources to satisfy a service
	 * level
	 */
	Scalability("REQ_SCA"),
	/**
	 * Traces and verify the history, location or application of an item by means of
	 * documented recorded identification
	 */
	Traceability("REQ_TRAC"),
	/**
	 * Maintain or recovery of operational state during a contextual and/or
	 * technical crisis situation
	 */
	Recoverability("REQ_RECO"),
	/**
	 * Keep a piece of equipment, a system or a whole installation in a safe and
	 * reliable functioning condition, according to pre-defined operational
	 * requirements
	 */
	Operability("REQ_OPE"),
	/**
	 * Evaluate obtained evidences regarding systems are safeguarding assets,
	 * maintaining data integrity, and operating effectively to achieve their tasks
	 */
	Auditability("REQ_AUD"),
	/**
	 * Provide a condition for users to perform the tasks safely, effectively, and
	 * efficiently while enjoying the experience
	 */
	Usability("REQ_USA"),
	/** Confidentiality, Integrity and Availability of information */
	Security("REQ_SEC"),
	/** Business domain key feature and/or functional scope */
	Functional("REQ_FCT"),
	/**
	 * The less effort and cost is required to achieve the same level of security.
	 * This principle, known as shifting left, is critically important regardless of
	 * the SDLC model. Shifting left minimizes any technical debt that would require
	 * remedied early security flaws late in development or after the software is in
	 * production.
	 */
	SoftwareDevelopmentLifeCycle("REQ_SDLC");

	private String acro;

	private RequirementCategory(String acronym) {
		this.acro = acronym;
	}

	/**
	 * The acronym of this requirement type (e.g used in unique identifier
	 * nomenclature)
	 * 
	 * @return A short label.
	 */
	public String acronym() {
		return this.acro;
	}

}
