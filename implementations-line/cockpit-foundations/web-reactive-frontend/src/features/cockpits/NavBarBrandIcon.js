import React from "react";
import logo from "../../media/cybnity-gorilla-light.svg";
import {Col, Container, Image, Row} from "react-bootstrap";
import {HiMenu} from "react-icons/hi";
import {Button} from "react-bootstrap/";

/**
 * Icon of cockpit screens manager.
 * @returns {Element}
 * @constructor
 */
export default function NavBarBrandIcon({logoOnClickHandler, menuOnClickHandler}) {

    return (
        <Container className="cockpit-tab-container">
            <Row className="cockpit-tab-container-row">
                <Col className="cockpit-tab-container-col">
                    <Button className="tab-bar-logo-button">
                        <Image className="tab-bar-logo"
                               alt="CYBNITY logo"
                               src={logo} onClick={logoOnClickHandler}
                        /></Button>
                </Col>
                <Col>
                    <HiMenu className="tab-bar-icon" onClick={menuOnClickHandler}/>
                </Col>
            </Row>
        </Container>
    );

};
