import {useDispatch, useSelector} from "react-redux";
import React from "react";
import {Button, Container, Form, Navbar} from "react-bootstrap";

/**
 * Header of cockpit panel screen.
 * @returns {Element}
 * @constructor
 */
export default function PanelScreenHeader(props) {
    const infoconStatusLevel = useSelector((state) => state.infoconLevel.level);
    const dispatch = useDispatch();

    /**
     * Customize the presentational components and accessible features according to the current organization's infocon level.
     * @param infoconLevel Current level of Infocon
     */
    function reconfigure(infoconLevel) {
        if (infoconLevel) {
            // Define which visual element shall be customized (color schema, active/deactive, panels closure) according to user's role and expected focus of him during the infocon level period
        }
    }

    return (
        <Navbar className="cockpit-panel-screen-header">
            <Container fluid>
                {(props.title && props.title !=="") && <Navbar.Collapse className="justify-content-start">
                    <Navbar.Text className="screen-panel-title">{props.title}</Navbar.Text>
                </Navbar.Collapse>
                }
                <Navbar.Collapse className="justify-content-center" id="navigatorForm">
                    {props.activatedNavigator && <Form className="d-flex">
                        <Form.Control
                            type="text"
                            placeholder="Search"
                            className="me-2"
                            aria-label="Search"
                        />
                        <Button type="submit">Submit</Button>
                    </Form>
                    }
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );

};
