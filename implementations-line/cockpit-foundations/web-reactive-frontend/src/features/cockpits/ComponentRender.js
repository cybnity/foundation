import React from "react";
import ActPanelScreenDisplay from "./act/ActPanelScreenDisplay";
import SituationPanelScreenDisplay from "./see/SituationPanelScreenDisplay";

/**
 * Presentation function ensuring the dynamic identification, instantiation and rendering of a reusable visual component.
 * @returns {*}
 * @constructor
 * @param componentName
 */
const ComponentRender = ({componentName}) => {
    /**
     * Mapping dynamically reusable component (for example into opened cockpit views)
     * @type {{ActPanelScreenDisplay: ((function(): Element)|*), SituationPanelScreenDisplay: ((function(): Element)|*)}}
     */
    const componentMapping = {
        ActPanelScreenDisplay, SituationPanelScreenDisplay
    };
    const Component = componentMapping[componentName];
    return (
        <Component key={Component.objectID}/>
    );
};

export default ComponentRender;