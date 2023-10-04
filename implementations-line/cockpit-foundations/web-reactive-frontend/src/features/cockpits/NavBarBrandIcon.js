import React from "react";
import logo from "../../media/cybnity-gorilla-light.svg";
import {Col, Container, Row} from "react-bootstrap";
import {HiMenu} from "react-icons/hi";
import {Button} from "react-bootstrap/";

/**
 * Icon of cockpit screens manager.
 * @returns {Element}
 * @constructor
 */
export default function NavBarBrandIcon({menuOnClickHandler}) {

    return (
        <Container>
            <Row>
                <Col>
                    <img
                        alt="CYBNITY logo"
                        src={logo}
                        width="30"
                        height="30"
                    />
                </Col>
                <Col>
                    <HiMenu onClick={menuOnClickHandler}/></Col>
            </Row>
        </Container>
    );

};
