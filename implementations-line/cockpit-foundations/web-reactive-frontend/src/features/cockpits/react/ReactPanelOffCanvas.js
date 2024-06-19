import Canvas from 'react-bootstrap/Offcanvas';
import {Offcanvas} from "react-bootstrap";
import React, {useState} from "react";
import ReactPanelScreenDisplay from "./ReactPanelScreenDisplay";

/**
 * Offcanvas including notification and means allowing user to react on events (e.g notification of event, action to perform).
 * @param name
 * @param props
 * @returns {Element}
 * @constructor
 */
export default function ReactPanelOffCanvas({name, ...props}) {
    const [show, setShow] = useState(false);

    const handleClose = () => setShow(false);

    return (
        <>
            <Offcanvas {...props}>
                <Canvas.Header closeButton>
                    <Canvas.Title>REACT</Canvas.Title>
                </Canvas.Header>
                <Canvas.Body>
                    <ReactPanelScreenDisplay />
                </Canvas.Body>
            </Offcanvas>
        </>
    );
}