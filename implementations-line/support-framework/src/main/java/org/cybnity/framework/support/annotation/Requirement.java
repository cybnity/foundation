package org.cybnity.framework.support.annotation;

import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(CLASS)
@Target({ TYPE, METHOD, PARAMETER, LOCAL_VARIABLE, PACKAGE, TYPE_PARAMETER })
/**
 * Annotation targeting a CYBNITY requirement that is supported by a source code
 * element.
 */
public @interface Requirement {

	/**
	 * An unique identifier of the CYBNITY requirement.
	 *
	 * @return Identifier label without the category acronym. For example, a label
	 *         equals to REQ_SEC_8310_AC2 identifier regarding a link to a countermeasure
	 *         specification identified as Security Requirement
	 *         Identifier (SRID) in policy specification document.
	 */
	String reqId();

	/**
	 * The type of requirement that is supported.
	 *
	 * @return The category of the requirement supported.
	 */
	RequirementCategory reqType();

}
