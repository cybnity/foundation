Feature: Create process
  Build of process respecting a templated structure (e.g defining by a referential)

  Scenario Outline: Company risk manager create a risk process management based on a standard referential as his company's default risk management process 
    Given A <referential> and <processName> template name is selected as ready for reuse
    When I try to build a process instance respecting the template <description>
    Then I get a standard <type> process instance deployed into the company
    Then <stepsQty> steps are defined as process staging
    
    Examples:
    | referential | processName |     description                     |           type          | stepsQty |
    |    "NIST"   |    "RMF"    | "Risk management framework process" | "cybersecurity process" |    7     |