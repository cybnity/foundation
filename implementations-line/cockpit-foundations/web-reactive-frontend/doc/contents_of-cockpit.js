import ActPanelScreenDisplay from "../src/features/cockpits/act/ActPanelScreenDisplay";
import Home from "../src/components/Home";
import CollaboratePanelScreenDisplay from "../src/features/cockpits/collaborate/CollaboratePanelScreenDisplay";
import ReactPanelScreenDisplay from "../src/features/cockpits/react/ReactPanelScreenDisplay";
import React from "react";

<ActPanelScreenDisplay/>

<Home/>

<p>visual elements assembled by the display component retained according to the connected
    user role and
    to the current Infocon level</p>

<p>use of HTML5 css regarding the Viewport Canvas:</p>
<div># viewport &#x7B;
    position : relative; // canvas elements inside it will be relative to the parent
    (cockpit)
    &#x7D;
    #viewport canvas &#x7B;
    position : absolute;
    &#x7D;
    # canvas &#x7B;
    background-color : transparent; // let other canvas rendre through
    &#x7D;
</div>

<CollaboratePanelScreenDisplay/>

<ReactPanelScreenDisplay/>