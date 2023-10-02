import CockpitScreen from "./features/cockpits/CockpitScreen";
import Home from "./components/Home";
import AccountRegistrationViewRendering from "./features/access-control/AccountRegistrationViewRendering";
import {BrowserRouter as Router, Routes, Route, Link,} from "react-router-dom";
import NoMatch from "./components/NoMatch";
import DynamicStandaloneInformationScreen from "./components/DynamicStandaloneInformationScreen";

export default function App() {

    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/cockpit" element={<CockpitScreen/>}/>
                <Route path="/account_registration" element={<AccountRegistrationViewRendering/>}/>
                <Route path="/manage/:type/:objectId" element={<DynamicStandaloneInformationScreen />}/>
                <Route path="*" element={<NoMatch/>}/>
            </Routes>
        </Router>
    );
};
