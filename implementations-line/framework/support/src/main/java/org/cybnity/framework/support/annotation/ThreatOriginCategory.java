package org.cybnity.framework.support.annotation;

/**
 * Type of origin regarding a threat (e.g provider of technology cyberthreat
 * notifications categorized by a specific identification pattern).
 */
public enum ThreatOriginCategory {
	/** Common Vulnerability Exposure published by Mitre organization */
	CVE("CVE");

	private String acro;

	private ThreatOriginCategory(String acronym) {
		this.acro = acronym;
	}

	/**
	 * The acronym of this requirement type (e.g used in unique identifier
	 * nomenclature by an external organization)
	 * 
	 * @return A short label.
	 */
	public String acronym() {
		return this.acro;
	}

}
