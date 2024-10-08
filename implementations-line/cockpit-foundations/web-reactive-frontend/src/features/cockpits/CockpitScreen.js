import React, {useState} from 'react';
import {useSelector, useDispatch} from 'react-redux';
import Tab from 'react-bootstrap/Tab';
import {
    perspectiveActivated, perspectiveClosed, perspectiveOpened
} from './CockpitPerspectivesContainer'
import ComponentRender from "./ComponentRender";
import {MdClose} from 'react-icons/md';
import PerspectiveTitleIconConfig from "../../components/icons/PerspectiveTitleIconConfig";
import {TfiNewWindow} from 'react-icons/tfi';
import NavBarBrandIcon from "./NavBarBrandIcon";
import NavigationShortcutsOffCanvas from "./navigate/NavigationShortcutsOffCanvas";
import CollaboratePanelNavbar from "./collaborate/CollaboratePanelNavbar";
import NavBarReactIcon from "./NavBarReactIcon";
import ReactPanelOffCanvas from "./react/ReactPanelOffCanvas";
import {Col, Nav, Row} from "react-bootstrap";
import {activatePerspective, closePerspective, openExternalizedPerspective} from "./ActionCreatorReferential";

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

    // Control of navigation shortcuts
    const [navigationShortcutsShow, setNavigationShortcutsShow] = useState(false);
    const handleNavigationShortcutsClose = () => setNavigationShortcutsShow(false);

    /**
     * Callback controlling the Offcanvas presentation component regarding information sections shortcuts.
     */
    function handleNavigationShortcutsShow() {
        setNavigationShortcutsShow(true);
    }

    /**
     * Callback opening the default ISMS control synthesis as home screen (e.g KPI, ISMS report overview) as current situation screen.
     */
    function handleCockpitControlHome() {
        dispatch(
            perspectiveOpened(
                openExternalizedPerspective('control' /* UUID of information screen*/, 'CONTROL' /* Title of the information screen*/, 'SituationPanelScreenDisplay'
                    , true, false, false, '')
            ));
    }

    // Control of notification/react panel
    const [reactPanelShow, setReactPanelShow] = useState(false);
    const handleReactPanelClose = () => setReactPanelShow(false);

    /**
     * Callback controlling the Offcanvas presentation component regarding react panel.
     */
    function handleReactPanelShow() {
        setReactPanelShow(true);
    }

    /**
     * Open an externalized screen over a new browser tab
     * @param objectType Type of information that shall be opened in new browser.
     * @param informationId Unique identifier of information that is managed by the opened new screen.
     */
    function openExternalized(objectType, informationId) {
        if (objectType && informationId) {
            // Build url location and internal view to show in new browser tab to open
            // Reuse the Route path defined in App.js regarding dynamic display to open (e.g /manage path)
            // and set the URL parameters
            let url = "/managed-content/" + objectType + "/" + informationId;
            const win = window.open(url, '_blank');
            if (win != null) {
                // Give focus to externalized view
                win.focus();
            }
            // Close original tab
            dispatch(
                perspectiveClosed(
                    closePerspective(informationId)))
        }
    }

    return (
        <div>
            <Tab.Container id="perspectivesTabs" defaultActiveKey={currentPerspective} activeKey={currentPerspective}>
                <Row>
                    <Col xs="auto">
                        <NavBarBrandIcon logoOnClickHandler={handleCockpitControlHome}
                                         menuOnClickHandler={handleNavigationShortcutsShow}/>
                    </Col>
                    <Col>
                        <Nav variant="tabs" onSelect={(eventKey, event) => {
                            dispatch(perspectiveActivated(
                                activatePerspective(eventKey)
                            ));
                        }}>
                            {perspectivesList.map((item) => <Nav.Link key={item.id} eventKey={item.id}>{<span>
                            {!item.imageMode && item.title}
                                    {(item.titleImage && item.titleImage.length > 0) &&
                                        <ComponentRender componentName={item.titleImage} onClick={(event) => {
                                            event.stopPropagation(); // Avoid propagation to tabs object (parent else which call onSelect)
                                        }}/>
                                    }
                                    {(item.exportable || item.closable) && <PerspectiveTitleIconConfig>
                                        {item.exportable &&
                                            <TfiNewWindow className="tab-title-icon" onClick={(event) => {
                                                event.stopPropagation(); // Avoid propagation to tabs object (parent else which call onSelect)
                                                console.log("Request externalization of view (id: " + item.id + ") into independent browser");
                                                openExternalized('risk', item.id);
                                            }}/>}
                                        {item.closable &&
                                            <MdClose className="tab-title-icon" onClick={(event) => {
                                                event.stopPropagation(); // Avoid propagation to tabs object (parent else which call onSelect)
                                                dispatch(
                                                    perspectiveClosed(
                                                        closePerspective(item.id)))
                                            }}/>
                                        }
                                    </PerspectiveTitleIconConfig>
                                    }
                                    </span>}</Nav.Link>
                            )}
                        </Nav>
                    </Col>
                    <Col xs="auto">
                        <NavBarReactIcon reactPanelOnClickHandler={handleReactPanelShow}/>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <Tab.Content>
                            {perspectivesList.map((item) => <Tab.Pane eventKey={item.id} key={item.id}>
                                    {item.componentName && <ComponentRender componentName={item.componentName}/>}
                                </Tab.Pane>
                            )}
                        </Tab.Content>
                    </Col>
                </Row>
            </Tab.Container>
            <NavigationShortcutsOffCanvas show={navigationShortcutsShow} onHide={handleNavigationShortcutsClose}
                                          key='informationDomainShortcutsPanel' placement='start'
                                          name='informationDomainShortcutsPanel'/>
            <ReactPanelOffCanvas show={reactPanelShow} onHide={handleReactPanelClose}
                                 key='reactPanel' placement='end'
                                 name='reactPanel'/>
            <CollaboratePanelNavbar id='communicationsBar'/>
        </div>
    );
}