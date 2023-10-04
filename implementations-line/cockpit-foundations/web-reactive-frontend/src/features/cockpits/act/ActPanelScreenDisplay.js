import {useDispatch, useSelector} from "react-redux";
import React from "react";
import {Col, Container, Row} from "react-bootstrap";

/**
 * Main information management screen that allow to manage any type of unique information or multiple sub-panels (e.g section of information managed).
 * @returns {Element}
 * @constructor
 */
export default function ActPanelScreenDisplay() {
    const infoconStatusLevel = useSelector((state) => state.infoconLevel.level);
    const dispatch = useDispatch();

    /**
     * Customize the presentational components and accessible features according to the current organization's infocon level.
     * @param infoconLevel Current level of Infocon
     */
    function reconfigure(infoconLevel) {
        if (infoconLevel) {
            // Define which visual element shall be customized (e.g color schema, active/deactive, panels closure) according to user's role and expected focus of him during the infocon level period
        }
    }

    return (
        <>
            <Container fluid className="vh-100 overflow-auto">
                <Row>
                    <Col>
                        <h1>ACT PANEL SCREEN DISPLAY</h1>
                        <p>INFOCON CURRENT STATUS: {infoconStatusLevel}</p>
                        <p>SEARCH BUTTON OPENING NavigateScreenDisplay</p>

                        <p>
                            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut
                            labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco
                            laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in
                            voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat
                            cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                        </p>
                    </Col>
                </Row>
            </Container>
        </>
    );

};
