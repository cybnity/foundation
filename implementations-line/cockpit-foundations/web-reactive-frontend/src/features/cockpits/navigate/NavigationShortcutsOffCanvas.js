import Canvas from 'react-bootstrap/Offcanvas';
import {Offcanvas} from "react-bootstrap";
import React, {useState} from "react";

/**
 * Offcanvas including a list of information sections (e.g menu items) that allow to directly open predetermined managed information screens.
 * @param name
 * @param props
 * @returns {Element}
 * @constructor
 */
export default function NavigationShortcutsOffCanvas({name, ...props}) {
    const [show, setShow] = useState(false);

    const handleClose = () => setShow(false);

    return (
        <>
            <Offcanvas {...props}>
                <Canvas.Header closeButton>
                    <Canvas.Title>DOMAIN SHORTCUTS</Canvas.Title>
                </Canvas.Header>
                <Canvas.Body>
                    ISMS domains list according to the current user's roles and current mission allowed to be
                    executed during the infocon status level
                </Canvas.Body>
            </Offcanvas>
        </>
    );
}