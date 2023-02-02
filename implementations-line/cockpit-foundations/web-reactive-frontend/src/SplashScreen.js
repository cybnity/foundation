import React, {PureComponent} from "react";
import Spinner from 'react-bootstrap/Spinner';

type PassedProps = {
    keycloakReady: boolean;
}

class SplashScreen extends PureComponent<PassedProps, any> {
    constructor(props: PassedProps) {
        super(props);
    }

    render() {
        //console.log(`SplashScreen keycloak ready ${this.props.keycloakReady}`);
        if(!this.props.keycloakReady){
            return <div><Spinner animation="border" role="status"><span className="visually-hidden">Loading...</span></Spinner></div>
        }else{
            return this.props.children
        }
    }
}

export default SplashScreen
