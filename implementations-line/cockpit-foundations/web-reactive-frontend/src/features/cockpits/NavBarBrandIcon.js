import React from "react";

import logo from "../../media/cybnity-gorilla-light.svg";
import {Col, Container, Row} from "react-bootstrap";
import {HiMenu} from "react-icons/hi";

/**
 * Icon of cockpit screens manager.
 * @returns {Element}
 * @constructor
 */
export default function NavBarBrandIcon() {

    return (
        <Container>
            <Row className="justify-content-md-center">
                <Col md="auto">
                    <img
                        alt=""
                        src={logo}
                        width="30"
                        height="30"
                        className="d-inline-block align-middle"
                    /></Col>
                <Col md="auto">
                    <HiMenu onClick={(event) => {
                        console.log("open menu");
                    }}/></Col>
            </Row>
        </Container>
    );

};
