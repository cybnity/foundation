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
        <Container fluid className="cockpit-tab-container">
            <Row className="cockpit-tab-container-row">
                <Col className="cockpit-tab-container-col">
                    <GoBell onClick={reactPanelOnClickHandler} className="tab-bar-icon"/>
                </Col>
            </Row>
        </Container>
    );

};
