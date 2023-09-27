import {useDispatch, useSelector} from "react-redux";
import React from "react";
import NavigateScreenDisplay from "../navigate/NavigateScreenDisplay";

/**
 * Main information management screen that allow to manage any type of unique information or multiple sub-panels (e.g section of information managed).
 * @returns {Element}
 * @constructor
 */
export default function ActPanelScreenDisplay() {
    const infoconStatusLevel = useSelector((state) => state.infoconLevel.level);
    const dispatch = useDispatch();

    /**
     * Customize the presentational components and accessible features according to the current organization's infocon level.
     * @param infoconLevel Current level of Infocon
     */
    function reconfigure(infoconLevel) {
        if (infoconLevel) {
            // Define which visual element shall be customized (e.g color schema, active/deactive, panels closure) according to user's role and expected focus of him during the infocon level period
        }
    }

    return (
        <>
            <h1>ACT PANEL SCREEN DISPLAY</h1>
            <p>INFOCON CURRENT STATUS: {infoconStatusLevel}</p>
            <p>SEARCH BUTTON OPENING NavigateScreenDisplay</p>
        </>
    );

};
