import {useDispatch, useSelector} from "react-redux";
import React from "react";

/**
 * Collaboration panel allowing to initialize/manage discussions with one or several other stakeholders (e.g chat, videoconf).
 * @returns {Element}
 * @constructor
 */
export default function CollaboratePanelScreenDisplay() {
    const infoconStatusLevel = useSelector((state) => state.infoconLevel.level);
    const dispatch = useDispatch();

    return (
        <>
            <h1>COLLABORATE PANEL SCREEN DISPLAY</h1>
            <p>INFOCON CURRENT STATUS: {infoconStatusLevel}</p>
            <p>use of ReactBoostrap component named OffCanvas</p>
        </>
    );

};
