import { BrowserRouter, Route, Routes } from 'react-router-dom';
import NavBar from './components/NavBar';
import Home from './components/Home';
import AccountRegistration from './features/access-control/AccountRegistration';
import Login from './components/Login';

export default function App() {
	return (
		<BrowserRouter>
			<div className="App">
				<NavBar />
				<Routes>
					<Route exact path='/' element={<Home />} />
					<Route path='/account_registration' element={<AccountRegistration />} />
					<Route path="/login" element={<Login />} />
				</Routes>
			</div>
		</BrowserRouter>
	);
};
