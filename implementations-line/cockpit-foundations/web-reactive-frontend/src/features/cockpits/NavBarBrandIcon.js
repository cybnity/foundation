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
        <Container className="cockpit-tab-container" fluid>
            <Row className="justify-content-xs-center" xs={2} md={2} lg={2}>
                <Col>
                    <Button className="tab-bar-logo-button">
                        <Image className="tab-bar-logo"
                               alt="CYBNITY logo"
                               src={logo} onClick={logoOnClickHandler}
                        /></Button>
                </Col>
                <Col xs="auto">
                    <HiMenu className="tab-bar-icon" onClick={menuOnClickHandler}/>
                </Col>
            </Row>
        </Container>
    )
        ;

};
