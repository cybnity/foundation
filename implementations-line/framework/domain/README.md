## PURPOSE
Presentation of the domain components regarding architecture components respecting the domain-driven design patterns.

# FUNCTIONAL VIEW
Presentation of the main functionalities area which allow realization of DDD requirements.

```mermaid
%%{
  init: {
    'theme': 'base',
    'themeVariables': {
        'background': '#ffffff',
        'fontFamily': 'arial',
        'fontSize': '10px',
        'primaryColor': '#fff',
        'primaryTextColor': '#0e2a43',
        'primaryBorderColor': '#0e2a43',
        'secondaryColor': '#fff',
        'secondaryTextColor': '#fff',
        'secondaryBorderColor': '#fff',
        'tertiaryColor': '#fff',
        'tertiaryTextColor': '#fff',
        'tertiaryBorderColor': '#fff',
        'edgeLabelBackground':'#fff',
        'lineColor': '#0e2a43',
        'titleColor': '#fff',
        'textColor': '#fff',
        'lineColor': '#0e2a43',
        'nodeTextColor': '#fff',
        'nodeBorder': '#0e2a43',
        'noteTextColor': '#fff',
        'noteBorderColor': '#fff'
    }
  }
}%%
flowchart BT
  subgraph global
    direction BT
    subgraph 4
      id9((PUBLISHED LANGUAGE))
    end
    subgraph 3
      direction BT
      id14 -- names enter --> id18
      id17((CORE DOMAIN)) -- avoid overinvesting in --> id16((GENERIC SUBDOMAINS))
      id5((CONTEXT MAP)) -- segregate the conceptual messes --> id6((BIG BALL OF MUD))
      id14 -- assess / overview relationships with --> id5
      id5 -- translate and insulate unilaterally with --> id7((ANTI-CORRUPTION LAYER))
      id5 -- free teams to go --> id8((SEPARATE WAYS))
      id5 -- minimize translation --> id11((CONFORMIST))
      id5 -- relate allied contexts as --> id12((CUSTOMER / SUPPLIER))
      id5 -- overlap allied contexts through --> id13((SHARED KERNEL))
      id5 -- interdependent contexts from --> id13
      id14((BOUNDED CONTEXT))
      id5 -- support multiple clients through --> id10((OPEN HOST SERVICE))
      id10 -- formalize --> id9
      id5 -- loosely couple contexts through --> id9
      id14 -- keep model unified by --> id15
    end
    subgraph 2
      direction BT
      id3 -- model gives structure to --> id18((UBIQUITOUS LANGUAGE))
      id3 -- isolate domain expressions with --> id19((LAYERED ARCHITECTURE))
      id3 -- express change with --> id2
      id4 -- push state change with --> id2((DOMAIN EVENTS))
      id17 -- cultivate rich model with --> id18
      id3 -- define model within --> id14
      id3((MODEL-DRIVEN DESIGN))
      id4 -- act as root of --> id22((AGGREGATES))
      id4 -- encapsulate with --> id22
      id4 -- access with --> id23((REPOSITORIES))
      id22 -- access with --> id23
      id3 -- express model with --> id1((SERVICES))
      id3 -- express identity with --> id4((ENTITIES))
      id15((CONTINUOUS INTEGRATION))
      id3 -- express state & computation with --> id20((VALUE OBJECTS))
      id20 -- encapsulate with --> id22
      id22 -- encapsulate with --> id21
      id4 -- encapsulate with --> id21
    end
    subgraph 1
      direction BT
      id20 -- encpasulate with --> id21((FACTORIES))
    end
  end
  classDef future stroke-dasharray: 5 5
  class id5,id6,id7,id8,id9,id10,id11,id12,id13,id16,id17,id18,id21 future;

```

# DESIGN VIEW

- [Structure models presentation](designview-structure-models.md) that give an overview of some key components.
- [Sub-packages structure models](designview-packages.md) detailing specific sub-packages contents.

Several unit tests are implemented into the Maven project and propose examples of best usage of the framework domain elements (e.g for developer help who can reuse the library's elements).

# RELEASES HISTORY
- [V0 - FRAMEWORK changes list](v0-changes.md)

#
[Back To Home](../README.md)
