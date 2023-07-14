package org.cybnity.feature.defense_template;

import org.cybnity.framework.immutable.Unmodifiable;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

/**
 * Represent a reference to a standard (e.g ISO/IEC) or external framework (e.g
 * NIST CSF) or official regulation (e.g GDPR) that can be referenced as frame
 * of topics specification.
 * 
 * @author olivier
 *
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public interface IReferential extends Unmodifiable {

	/**
	 * Get the label acronym defined this referential.
	 * 
	 * @return An acronym (e.g NIST).
	 */
	public String acronym();

	/**
	 * Get the label naming this referential.
	 * 
	 * @return A label (e.g National Institute of Standards and Technology).
	 */
	public String label();
}
