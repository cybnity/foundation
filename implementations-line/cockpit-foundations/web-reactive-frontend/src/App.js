import { BrowserRouter, Route, Routes } from 'react-router-dom';
import NavBar from './components/NavBar';
import Home from './components/Home';
import TenantRegistration from './components/access-control/TenantRegistration';
import UserAccountRegistration from './components/access-control/UserAccountRegistration';
import Resources from './components/Resources';

export default function App() {
	return (
		<div>
			<BrowserRouter>
				<div className="App">
					<NavBar />

					<Routes>
						<Route path='/' element={<Home />} />
						<Route path='/organization_signup' element={<TenantRegistration />} />
						<Route path='/useraccount_signup' element={<UserAccountRegistration />} />
						<Route path="/resource" element={<Resources />} />
					</Routes>
				</div>
			</BrowserRouter>
		</div>
	);
};
