# RELEASE NOTES

Release Name: V0 - FRAMEWORK

# CHANGES
## BUG

## IMPROVEMENT

## ADDS
| Issue | Origin/Cause     | Description                                                                                                                                                                                                                                                   |
|:------|:-----------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 237   | REQ_CONS_8       | Type version of each fact record is based on origin fact natural identifier, and is generated like hashed version of minimum 88 characters; version strategy for class types serial number is based on a hashed version using 512-bit SHA-2 hashing algorithm |
| 58    | REQ_SEC_3        | Tenant support for users of same organization                                                                                                                                                                                                                 |
| 145   | REQ_ROB_3        | Application-agnostic fact reading for storage                                                                                                                                                                                                                 |
| 157   | REQ_MAIN_10      | Separate architecture concerns (specification vs implementation) over multiple libray projects                                                                                                                                                                |
| 61    | REQ_MAIN_1       | Deployable module per application (bounded context)                                                                                                                                                                                                           |
| 153   |                  | Easy link of any requirement (e.g architecture, security, functional) via annotations                                                                                                                                                                         |
| 149   | REQ_MAIN_5       | Immutable architecture structural design patterns                                                                                                                                                                                                             |
| 158   | REQ_SCA_4        | Support of CQRS pattern and event sourcing concerns combined                                                                                                                                                                                                  |
| 136   | REQ_CONS_3       | Support of immutable cardinality and ordered fact data (predecessors)                                                                                                                                                                                         |
| 159   | REQ_SEC_8370_CM6 | Basic configuration variables support; Context provider of environment variable (e.g component configuration); ExecutableComponentChecker allowing check of healthy and operable state of a started component                                                 |

# KNOWN ISSUES
None. Do not hesitate to report any problem.
