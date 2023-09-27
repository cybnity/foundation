
import {useDispatch, useSelector} from "react-redux";
import React from "react";

/**
 * Reaction panel allowing to see and quickly initialize an expected behavior from the user.
 * @returns {Element}
 * @constructor
 */
export default function ReactPanelScreenDisplay() {
    const infoconStatusLevel = useSelector((state) => state.infoconLevel.level);
    const dispatch = useDispatch();

    return (
        <>
            <h1>REACT PANEL SCREEN DISPLAY</h1>
            <p>INFOCON CURRENT STATUS: {infoconStatusLevel}</p>
            <p>use of ReactBoostrap component named OffCanvas</p>
        </>
    );

};
