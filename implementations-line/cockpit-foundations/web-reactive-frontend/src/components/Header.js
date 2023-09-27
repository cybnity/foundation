import {Navbar, Container, Nav} from 'react-bootstrap';
import logo from '../media/cybnity-gorilla-light.svg';
import React from "react";

export default function Header() {
    return (
        <Navbar expand="lg">
            <Container fluid>
                <Navbar.Brand href="/">
                    <img
                        alt=""
                        src={logo}
                        width="30"
                        height="30"
                        className="d-inline-block align-top"
                    />
                </Navbar.Brand>
                <Nav className="me-auto" variant="tabs">
                    <Nav.Item>
                        <Nav.Link eventKey="home" href="home">HOME</Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                        <Nav.Link eventKey="control">ACCESS COCKPIT</Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                        <Nav.Link eventKey="signup">SIGN UP</Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                        <Nav.Link eventKey="login">LOG IN</Nav.Link>
                    </Nav.Item>
                </Nav>
            </Container>
        </Navbar>
    );
};
