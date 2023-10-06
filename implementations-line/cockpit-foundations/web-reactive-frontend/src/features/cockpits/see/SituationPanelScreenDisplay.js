import {useDispatch, useSelector} from "react-redux";
import React from "react";
import {Button, Col, Container, Navbar, Row} from "react-bootstrap";
import {
    perspectiveOpened
} from '../CockpitPerspectivesContainer'
import {openExternalizedPerspective} from "../ActionCreatorReferential";
import {Form} from "react-bootstrap/";

/**
 * Situation panel allowing to present information (e.g dashboard of KPI) not dedicated to be managed but focused on visualization (e.g graph navigation).
 * @returns {Element}
 * @constructor
 */
export default function SituationPanelScreenDisplay() {
    const infoconStatusLevel = useSelector((state) => state.infoconLevel.level);
    const dispatch = useDispatch();

    return (
        <div className="vh-100 overflow-auto">
            <Navbar className="cockpit-panel-screen-header" id="navigationSearch">
                <Container fluid>
                    <Navbar.Collapse className="justify-content-center">
                        <Form className="d-flex">
                            <Form.Control
                                type="text"
                                placeholder="Search"
                                className="me-2"
                                aria-label="Search"
                            />
                            <Button type="submit">Submit</Button>
                        </Form>
                    </Navbar.Collapse>
                </Container>
            </Navbar>
            <Container fluid>
                <Row>
                    <Col>
                        <p>INFOCON CURRENT STATUS: {infoconStatusLevel}</p>

                        <Button as="a" size={'lg'} variant={'primary'}
                                onClick={() => dispatch(
                                    perspectiveOpened(
                                        openExternalizedPerspective('informationUUI', 'Business Object A', 'ActPanelScreenDisplay'
                                            , true, true, false, '')
                                    ))}>Test Information Perspective Opening</Button>

                        <Button as="a" size={'lg'} variant={'secondary'}
                                onClick={() => dispatch(
                                    perspectiveOpened(
                                        openExternalizedPerspective('informationUUI2', 'Business Object B', 'ActPanelScreenDisplay'
                                            , true, true, false, '')
                                    ))}>Another Same Type of Information Perspective Opening</Button>
                    </Col>
                </Row>
            </Container>
        </div>
    );

};
