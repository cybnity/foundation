import {useDispatch, useSelector} from "react-redux";
import React from "react";
import {Button} from "react-bootstrap";
import {
    perspectiveOpened
} from '../CockpitPerspectivesContainer'

/**
 * Situation panel allowing to present information (e.g dashboard of KPI) not dedicated to be managed but focused on visualization (e.g graph navigation).
 * @returns {Element}
 * @constructor
 */
export default function SituationPanelScreenDisplay() {
    const infoconStatusLevel = useSelector((state) => state.infoconLevel.level);
    const dispatch = useDispatch();

    return (
        <>
            <h1>SITUATION PANEL SCREEN DISPLAY</h1>
            <p>INFOCON CURRENT STATUS: {infoconStatusLevel}</p>

            <Button as="a" size={'lg'} variant={'primary'}
                    onClick={() => dispatch(
                        perspectiveOpened({
                            type: 'OPEN_PERSPECTIVE',
                            id: 'informationUUI',
                            title: 'Business Object A',
                            componentName: 'ActPanelScreenDisplay',
                            closable: true,
                            exportable: true,
                            imageMode: false
                        }))}>Test Information Perspective Opening</Button>

            <Button as="a" size={'lg'} variant={'secondary'}
                    onClick={() => dispatch(
                        perspectiveOpened({
                            type: 'OPEN_PERSPECTIVE',
                            id: 'informationUUI2',
                            title: 'Business Object B',
                            componentName: 'ActPanelScreenDisplay',
                            closable: true,
                            exportable: true,
                            imageMode: false
                        }))}>Another Same Type of Information Perspective Opening</Button>
        </>
    );

};
