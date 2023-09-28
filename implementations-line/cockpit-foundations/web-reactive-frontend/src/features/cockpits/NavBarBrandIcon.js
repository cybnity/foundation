import React from "react";

import logo from "../../media/cybnity-gorilla-light.svg";
import Navbar from 'react-bootstrap/Navbar';

/**
 * Icon of cockpit screens manager.
 * @returns {Element}
 * @constructor
 */
export default function NavBarBrandIcon() {

    return (
        <img
            alt=""
            src={logo}
            width="30"
            height="30"
            className="d-inline-block align-middle"
        />
    );

};
