## PURPOSE
Presentation of several coding conventions followed by the developers implementing the CYBNITY projects.

# SOURCE CODES DOCUMENTATION
## Requirements reference linking
Specific annotations are available to link the specification documentations (e.g functional, architecture, security requirement...) managed in other repository than GitHub with the source codes developed as realization of them.

Why it's important: to quickly navigate and control the quality of alignment between the specifications managed in any other tools (e.g Notion tool for Product Requirements Definition, Security control measures and policies, architecture concepts) with the implementation software codes.

How: the __support framework library__ (dependency defined in parent `pom.xml` of any implementation project) provide specific reusable annotations for add link to requirement managed in an external documentation reference (link based on requirement identifier). The annotation is usable on several source code element types (e.g Method, Parameter, Package...).

For example, to add a reference to an architecture requirement (e.g identified as REQ_ARC_10) into a CYBNITY source code package (e.g into a `package-info.java` file):

```java
@CYBNITYRequirement(reqType = RequirementCategory.Maintainability, reqId = "10")
package org.cybnity.infrastructure.technical.message_bus.adapter.impl;

import org.cybnity.framework.support.annotation.CYBNITYRequirement;
import org.cybnity.framework.support.annotation.RequirementCategory;
```

## Vulnerabilities origin linking
Specific annotation is also available to add any references to known vulnerabilities (e.g generated by reused external technologies which not was fixed; or regarding a specific security mitigation developed into a CYBNITY component) fixed into a CYBNITY source code and/or configuration file.

Why it's important: some time some vulnerability are not quickly fixed by the technology partners or other open source projects, and CYBNITY program's developers can develop a fix code more quickly (e.g a temporary mitigation solution reducing the threat impact on the CYBNITY software including a dependency to the external problem) during the time for the partner to solve the problem into their software version.

How: the __support framework library__ (dependency defined in parent `pom.xml` of any implementation project) provide specific reusable annotations for add link to vulnerability declaring by external stakeholder (e.g other software editor) and/or public documentation (e.g Mitre website). The annotation is usable on several source code element types (e.g Type, Method, Local variable, Type parameter...).

For example, to add a reference link to a Mitre published vulnerability (e.g identified as CVE-2022-33915) on a java method fixing the problem during mitigation period into a CYBNITY source code file:

```java
import org.cybnity.framework.support.annotation.VulnerabilityOrigin;
import org.cybnity.framework.support.annotation.ThreatOriginCategory;

class X {
  @VulnerabilityOrigin(originType = ThreatOriginCategory.CVE, originId = "2022-33915")
  public methodWhereVulnerabilityGenerateImpact(...) {

  }
}
```

#
[Back To Parent](../)