import React from "react";
import {GoBell} from 'react-icons/go';
import {Col, Container, Row} from "react-bootstrap";

/**
 * Icons of cockpit screens manager relative to user notifications generating reactions.
 * @returns {Element}
 * @constructor
 */
export default function NavBarReactIcon({reactPanelOnClickHandler}) {

    return (
        <Container fluid>
            <Row className="justify-content-md-end">
                <Col md="auto">
                    <GoBell onClick={reactPanelOnClickHandler}/></Col>
            </Row>
        </Container>
    );

};
