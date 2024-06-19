import React from "react";
import {Container, Navbar} from "react-bootstrap";
import Offcanvas from "react-bootstrap/Offcanvas";

/**
 * Collaboration navigation bar allowing to show communication systems panel.
 * @returns {Element}
 * @constructor
 */
export default function CollaboratePanelNavbar() {

    return (
        <Navbar id='communicationsBar' expand='false' className="bg-body-secondary" fixed="bottom">
            <Container fluid>
                <Navbar.Brand href="#">INTERCOM</Navbar.Brand>
                <Navbar.Toggle aria-controls={'collaborate-panel-expand'}/>
                <Navbar.Offcanvas
                    id={'collaborate-panel-expand'}
                    aria-labelledby={'collaborate-panel-expand'}
                    placement="bottom">
                    <Offcanvas.Header closeButton>
                        <Offcanvas.Title id={'collaborate-panel-expand'}>
                            INTERCOM
                        </Offcanvas.Title>
                    </Offcanvas.Header>
                    <Offcanvas.Body>
                        Collaboration systems allowing team members quick exchanges (e.g instant chat, videoconference...)
                    </Offcanvas.Body>
                </Navbar.Offcanvas>
            </Container>
        </Navbar>
    );

};
