# RELEASE NOTES

Release Name: V0 - FRAMEWORK

# CHANGES
## BUG

## IMPROVEMENT

## ADDS
| Issue | Origin/Cause     | Description                                                                                                                                                                                                      |
|:------|:-----------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 237   | REQ_ROB_3        | DomainEventInMemoryStoreImpl in-memory EventStore implementation component providing persistence capability as generic fact table store reusable for states persistence system saving the data changes           |
| 237   | AC-2(8)          | ConcreteCommandEvent, ConcreteQueryEvent and ConcreteDomainChangeEvent added for access-control bounded context reuse. Factories added. Component presence observability capability. Event sourcing capabilities |
| 95    | REQ_FCT_73       | CompletionState, ITemplate, TemplateType added as reusable status for workflow state machine support. ConcreteDomainChangeEvent reusable as generic domain event                                                 |
| 58    | REQ_SEC_3        | Tenant support for users of same organization                                                                                                                                                                    |
| 145   | REQ_ROB_3        | Application-agnostic fact reading for storage                                                                                                                                                                    |
| 158   | REQ_SCA_4        | Support of CQRS pattern and event sourcing concerns combined                                                                                                                                                     |
| 154   | REQ_MAIN_7       | Support bounded context message coordination layer (Process Manager pattern)                                                                                                                                     |
| 55    | REQ_ROB_2        | Data location-independent identity generator                                                                                                                                                                     |
| 159   | REQ_SEC_8370_CM6 | Basic configuration variable support regarding a Write Model or a Read Model                                                                                                                                     |

# KNOWN ISSUES
None. Do not hesitate to report any problem.
