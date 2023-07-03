Feature: Create process
  Build of process respecting a templated structure (e.g defining by a referential)

  Scenario Outline: Company risk manager create a risk process management based on a standard referential as his company's default risk management process 
    Given A <referential> and <processName> template name is selected as ready for reuse
    When I try to build a process instance respecting the template <description>
    Then I get a standard <type> process instance deployed into the company
    
    Examples:
    | referential | processName |     description                                |           type          |
    |    "NIST"   |    "RMF"    | "Risk management framework process"            | "cybersecurity process" |
    |  "ISO/IEC"  |    "27005"  | "Information security risk management process" | "cybersecurity process" |
    |    "DTIC"   |    "D3A"    | "Targeting process management"                 |    "targeting process"  |
    |    "DTIC"   |    "F3EAD"  | "Targeting function management"                |    "targeting process"  |