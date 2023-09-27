import {ReactComponent as Logo} from './../media/cybnity-badge.svg';
import React from "react";
import {Link} from "react-router-dom";

export default function Home() {

    return (
        <div>
            <p></p>

            <Logo height="200px" width="200px"/>

            <h1>Welcome Home UI</h1>

            <p>Privacy Policy link</p>

            <nav>
                <Link to="/">
                    HOME
                </Link>
                <Link to="/cockpit">
                    COCKPIT ACCESS
                </Link>
                <Link to="/account_registration">
                    ACCOUNT REGISTRATION
                </Link>
            </nav>
        </div>
    );
};
