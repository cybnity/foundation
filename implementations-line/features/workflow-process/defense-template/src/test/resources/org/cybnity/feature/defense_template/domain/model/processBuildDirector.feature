Feature: Create a standardized process
  Build of process respecting a templated structure (e.g defining by an XML document)

  Scenario Outline: Company risk manager create a risk process management based on a standard referential as his company's default risk management process 
    Given <language> translation is selected to be apply on generated contents
    Given A <referential> and <processName> template is selected as ready for reuse
    When I try to build a process instance respecting the template <fullName>
    Then I get a standard <type> process instance deployed into the company
    And <stepsQty> phases are defined as process staging that are ordered and named <phaseNames>
    
    Examples:
    | referential | processName |        fullName             |           type          | stepsQty | language |                           phaseNames                                    |
    |    "NIST"   |    "RMF"    | "Risk management framework" | "cybersecurity process" |    7     |   "en"   |      "PREPARE,CATEGORIZE,SELECT,IMPLEMENT,ASSESS,AUTHORIZE,MONITOR"     |
    |    "NIST"   |    "RMF"    | "Risk management framework" | "cybersecurity process" |    7     |   "fr"   |  "PRÉPARE,CATÉGORISE,SÉLECTIONNE,IMPLÉMENTE,ÉVALUE,AUTHORISE,SURVEILLE" |