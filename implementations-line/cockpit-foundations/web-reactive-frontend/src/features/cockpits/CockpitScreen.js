import React, {useEffect, useState} from 'react';
import {useSelector, useDispatch} from 'react-redux';
import Tab from 'react-bootstrap/Tab';
import Tabs from 'react-bootstrap/Tabs';
import {Col, Container, Navbar, Row} from "react-bootstrap";
import logo from "../../media/cybnity-gorilla-light.svg";
import {
    perspectiveActivated, perspectiveClosed
} from './CockpitPerspectivesContainer'
import ComponentRender from "./ComponentRender";
import {MdClose} from 'react-icons/md';
import PerspectiveTitleIconConfig from "../../components/icons/PerspectiveTitleIconConfig";
import {TfiNewWindow} from 'react-icons/tfi';
import NavBarBrandIcon from "./NavBarBrandIcon";

/**
 * Cockpit composite view including the panels assembled and ready to be customized according to the user role, mission, and organization current infocon level.
 * @returns {Element}
 * @constructor
 */
export default function CockpitScreen() {
    // Get current infocon level state
    const infoconStatusLevel = useSelector((state) => state.infoconLevel.level);
    // Get current list of managed perspectives
    const perspectivesList = useSelector((state) => state.managedPerspectives.set);
    // Monitor current active perspective
    const currentPerspective = useSelector((state) => state.managedPerspectives.activePerspective);
    // Dispatcher to the reducers supporting the data layer control
    const dispatch = useDispatch();

    return (
        <Container fluid>
            <Row>
                <Col>
                    <Tabs
                        id="perspectivesTabs"
                        activeKey={currentPerspective}
                        onSelect={(eventKey, event) => {
                            dispatch(perspectiveActivated({type: 'ACTIVATE_PERSPECTIVE', perspectiveId: eventKey}));
                        }} className="mb-3">

                        <Tab title={<span><NavBarBrandIcon /></span>}>
                        </Tab>
                        {perspectivesList.map((item) => (
                            <Tab eventKey={item.id} title={<span>
                                {!item.imageMode && item.title}
                                {item.titleImage &&
                                    <ComponentRender componentName={item.titleImage} onClick={(event) => {
                                        event.stopPropagation(); // Avoid propagation to tabs object (parent else which call onSelect)

                                    }}/>
                                }
                                {(item.exportable || item.closable) &&
                                    <PerspectiveTitleIconConfig>
                                        {item.exportable &&
                                            <TfiNewWindow onClick={(event) => {
                                                event.stopPropagation(); // Avoid propagation to tabs object (parent else which call onSelect)
                                                console.log("Request externalization of view (id: " + item.id + ") into independent browser");
                                            }}/>}
                                        {item.closable &&
                                            <MdClose onClick={(event) => {
                                                event.stopPropagation(); // Avoid propagation to tabs object (parent else which call onSelect)
                                                dispatch(
                                                    perspectiveClosed({
                                                        type: 'CLOSE_PERSPECTIVE',
                                                        id: item.id
                                                    }))
                                            }}/>
                                        }
                                    </PerspectiveTitleIconConfig>}</span>} key={item.id}>
                                {item.componentName &&
                                    <ComponentRender componentName={item.componentName}/>
                                }
                            </Tab>
                        ))
                        }
                    </Tabs>
                </Col>
                <Col lg="1">
                    infolevel
                </Col>
            </Row>
        </Container>
    );
}