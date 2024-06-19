import {useDispatch, useSelector} from "react-redux";
import React from "react";

/**
 * Navigator panel into the information system via search feature and results view.
 * @returns {Element}
 * @constructor
 */
export default function NavigateScreenDisplay() {
    const infoconStatusLevel = useSelector((state) => state.infoconLevel.level);
    const dispatch = useDispatch();

    return (
        <>
            <h1>NAVIGATE SCREEN DISPLAY</h1>
            <p>INFOCON CURRENT STATUS: {infoconStatusLevel}</p>
        </>
    );

};
