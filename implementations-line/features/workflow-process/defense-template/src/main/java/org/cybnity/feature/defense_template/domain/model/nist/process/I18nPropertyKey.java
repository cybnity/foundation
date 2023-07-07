package org.cybnity.feature.defense_template.domain.model.nist.process;

/**
 * Key of property that is supported by the translation file.
 * 
 * @author olivier
 *
 */
public enum I18nPropertyKey {

	PROCESS_RMF_STAGING_STEP_PREPARE("nist.process.rmf.staging.step.prepare"),
	PROCESS_RMF_STAGING_STEP_CATEGORIZE("nist.process.rmf.staging.step.categorize"),
	PROCESS_RMF_STAGING_STEP_SELECT("nist.process.rmf.staging.step.select"),
	PROCESS_RMF_STAGING_STEP_IMPLEMENT("nist.process.rmf.staging.step.implement"),
	PROCESS_RMF_STAGING_STEP_ASSESS("nist.process.rmf.staging.step.assess"),
	PROCESS_RMF_STAGING_STEP_AUTHORIZE("nist.process.rmf.staging.step.authorize"),
	PROCESS_RMF_STAGING_STEP_MONITOR("nist.process.rmf.staging.step.monitor");

	private String key;

	private I18nPropertyKey(String key) {
		this.key = key;
	}

	/**
	 * Get the property key that allow to read the translated value hosted by the
	 * configuration file.
	 * 
	 * @return A key name.
	 */
	public String key() {
		return this.key;
	}
}
