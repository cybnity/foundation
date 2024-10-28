## PURPOSE
This documentation is presenting the UX and UI principles designed by CYBNITY as presentation layer that allow final user to use the software suite.
Find here some usage scenario of CYBNITY (e.g as a CISO's point of view) which show what are the UI concepts allowing to manage the security information, to collaborate with other stakeholder of security team, and that constituate the UI of ISMS managed by a company via CYBNITY solution.

### What is a Mockup?
A UI mockup is a visual representation of a final digital product (e.g ui cockpit of CYBNITY), including layout/hierarchy, color, typography, icons, and other UI elements. While mockups are high-fidelity designs, they are static and have no functionality-like a screenshot.

CYBNITY developed a System Design approach from some typical and important use test scenario to identify the key screens, functions, visual components and screen interactions that are required by user to execute their missions in an ergonomic and safe way.

The implementation documentation includes many types of support deliverables produced during the UX/UI system design life cycle.
You can find documentations relative to software maintenance like:
- UI Mockup per specific mission and/or operational goal
- Description of visual components presentation rules and organization
- Library of graphic elements (e.g color scheme) standardizing the CYBNITY user interfaces developed by UI developers

These technical documentations are supporting the UI components development.

# MAIN COCKPIT GUI
The Product Requirements Definition (PRD) of CYBNITY cockpit (main screens organization of CYBNITY web presentation layer as Graphic User Interface) is detailed per test scenario on [Cockpit PRD - Usage Scenarios Functional Tests](https://cybnity.notion.site/c0e0365212984a5eaf17e8f2d068ba6f?v=8b6bc1bbe4bb432da4b7937ef7eb4ed9&pvs=4).

Each scenario have been identified as a key and representative type of security use case which determine the visual behavior and/or interaction types that are required by a user with CYBNITY functions to reach a specific security mission (according to a security role like risk analysis, or CISO).

The global UI cockpit approach is inspired by the fighter jets cockpit organization which allow planification (e.g mission, operations), coordination (e. between team member), instant reaction (e.g during crisis or alert) and management of large information (e.g weapon systems, procedures, trajectory, environment metrics).

Usage Scenarios: Behavior-Driven Design (BDD) method is implemented for definition of each usage scenario into a test case driven approach. A scenario is always targeting a goal to reach that is measurable as an end fact (e.g produced by the cockpit user and/or a state changed into the defense system).

The scenario of usage are categorized per capability type covered by a CYBNITY application domain allowing to identify which application domain shall be owner of the user interface elements (presentation layer of application domain) required to have interaction with the provided features.

The link to specification documentation are mentionned like Scenario Detail Card for access to more detail on the usage scenario description.

## CAPABILITY - Actions & Scheduling
| Use Scenario Name | Specification Card | UI Mockup |
| :---------------- | :----------------- | :-------- |
| Permanent activities expected of a security team member are updated and aligned with his committed roles | [Scenario Card](https://cybnity.notion.site/Permanent-activities-expected-of-a-security-team-member-are-updated-and-aligned-with-his-committed-r-a8983c6abf21467782cf615109835372?pvs=4) | ![image](/mockups/Permanent-activities-are-updated-with-his-committed-roles.png) |
| Recipient’s attention of new assigned tasks is visually captured to animate his motivation to treat them | [Scenario Card](https://cybnity.notion.site/Recipient-s-attention-of-new-assigned-tasks-is-visually-captured-to-animate-his-motivation-to-treat--9aabdef3f9c94acdbc09cc0b6aa38ef6?pvs=4) | ![image](/mockups/Recipient-attention-is-visually-captured.png) |
| User is automatically alerted and notified of the exceeding of an acceptable threshold defined around a cyber risk score relating to a perimeter of assets (e.g information type) | [Scenario Card](https://cybnity.notion.site/User-is-automatically-alerted-and-notified-of-the-exceeding-of-an-acceptable-threshold-defined-aroun-bc180fa596814d409f2e0c5e16fa02e3?pvs=4) | ![image](/mockups/User-is-automatically-alerted-and-notified.png) |

## CAPABILITY - Missions & Programming
| Use Scenario Name | Specification Card | UI Mockup |
| :---------------- | :----------------- | :-------- |
| The treatment of an urgent high-level task is quickly started by its assigned actor who was not connected on a CYBNITY user interface | [Scenario Card](https://cybnity.notion.site/The-treatment-of-an-urgent-high-level-task-is-quickly-started-by-its-assigned-actor-who-was-not-conn-660e38282fbd4d738e003473ef0ffa7e?pvs=4) | ![image](/mockups/Treatment-of-task-is-quickly-started.png) |
| A task defined and assigned to the user is easily managed in accordance with its level of priority | [Scenario Card](https://cybnity.notion.site/A-task-defined-and-assigned-to-the-user-is-easily-managed-in-accordance-with-its-level-of-priority-fb1120b6dea744208933b53a08b1126c?pvs=4) | ![image](/mockups/task-defined-to-user-easily-managed.png) |

## CAPABILITY - Threat/Risks Prevention & Treatment
| Use Scenario Name | Specification Card | UI Mockup |
| :---------------- | :----------------- | :-------- |
| The established risk appreciation process is quickly started to frame the evaluation of a new asset | [Scenario Card](https://cybnity.notion.site/The-established-risk-appreciation-process-is-quickly-started-to-frame-the-evaluation-of-a-new-asset-905d902944a7402f871cd1a75d90c2e6?pvs=4) | ![image](/mockups/Established-risk-appreciation-process-is-quickly-started.png)|
| Severity of weaknesses or deficiencies regarding an asset and usage is identified via risk appreciation | [Scenario Card](https://cybnity.notion.site/Severity-of-weaknesses-or-deficiencies-regarding-an-asset-and-usage-is-identified-via-risk-appreciat-e394b38cc6904caf90c885dd6b23d862?pvs=4) | ![image](/mockups/Severity-of-weaknesses-is-identified-via-risk-appreciation.png) |

## CAPABILITY - ISMS & Strategy
| Use Scenario Name | Specification Card | UI Mockup |
| :---------------- | :----------------- | :-------- |
| Risk assessment process is edited and approved as new revision | [Scenario Card](https://cybnity.notion.site/Risk-assessment-process-is-edited-and-approved-as-new-revision-238252809d8b49048f9fafc721ca225b?pvs=4) | ![image](/mockups/Risk-assessment-process-is-edited-and-approved-as-new-revision.png)|

## CAPABILITY - Stakeholders & Responsibilities
| Use Scenario Name | Specification Card | UI Mockup |
| :---------------- | :----------------- | :-------- |
| Organization roles and responsibilities matrix is managed with visible approvals status regarding each stakeholder | [Scenario Card](https://cybnity.notion.site/Organization-roles-and-responsibilities-matrix-is-managed-with-visible-approvals-status-regarding-ea-e45aa6f8744842d988aac444fe55e7d5?pvs=4) | ![image](/mockups/Organization-roles-matrix-is-managed.png) |

#
[Back To Home](../README.md)
