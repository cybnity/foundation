import CockpitScreen from "./features/cockpits/CockpitScreen";
import Home from "./components/Home";
import AccountRegistrationViewRendering from "./features/access-control/AccountRegistrationViewRendering";
import {BrowserRouter as Router, Routes, Route} from "react-router-dom";
import NoMatch from "./components/NoMatch";
import DynamicStandaloneInformationScreen from "./components/DynamicStandaloneInformationScreen";
import React from "react";

/**
 * Main reactive application configuration supporting the dedicated cockpit screens.
 * See Restful definition at https://restfulapi.net/resource-naming/
 * @returns {JSX.Element}
 * @constructor
 */
export default function App() {

    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/cockpit" element={<CockpitScreen/>}/>
                <Route path="/account-registration" element={<AccountRegistrationViewRendering/>}/>
                <Route path="*" element={<NoMatch/>}/>
                <Route path="/managed-content/:type/:resourceId" element={<DynamicStandaloneInformationScreen/>}/>
            </Routes>
        </Router>
    );
};
