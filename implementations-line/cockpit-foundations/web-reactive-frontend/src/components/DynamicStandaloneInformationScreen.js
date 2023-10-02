/**
 * Independent composite screen allowing to show a dedicated and autonomous information (e.g into a standalone web browser view).
 * The automatic selection of a reactive assembled sub-screen is ensured according to the type of information that shall be managed.
 * This screen composer select the good sub-screen to assemble and delegate it the information to manage.
 */
import { useLocation, useParams } from 'react-router-dom';
import {Container} from "react-bootstrap";
export default function DynamicStandaloneInformationScreen(props) {

    const location = useLocation();
    const { type } = useParams();
    const { objectId } = useParams();

    console.log(location);
    console.log(type);
    console.log(objectId);

    // Identify the type of information to manage

    // Select the type of screen which shall be compatible with information to manage
    // based on <ActPanelScreenDisplay /> display
    // or selected from <ComponentRender componentName={viewComponent}/>
    // Identify the information identifier

    // Build the main standalone composed screen (e.g without tab, without search bar...) including the dedicated information type display

    return (
        <Container fluid>
            <p>
            standalone composed view based on information type to manage</p>
            <p>Type: {type}</p>
            <p>Information ID: {objectId}</p>
        </Container>
    );
}