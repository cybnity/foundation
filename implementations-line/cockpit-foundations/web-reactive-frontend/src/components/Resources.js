import { useState, useEffect } from 'react';
import Keycloak from 'keycloak-js';

export default function Resources() {
	// Check the keycloak instance and authentication
	const [keycloak, setKeycloak] = useState(null)
	const [authenticated, setAuthenticated] = useState(false)

	// Track the render cycle when the component is mounted onto the DOM to invoke the Keycloak instance
	useEffect(() => {
		const keycloak = Keycloak('/keycloak.json');

		keycloak.init({ onLoad: 'login-required', checkLoginIframe: false, silentCheckSsoFallback: false }).then(authenticated => {
			setKeycloak(keycloak);
			console.log("authenticated="+authenticated);
			if (authenticated) {
				window.accessToken = keycloak.token;
			}
			setAuthenticated(authenticated)
		})
	}, [])

	const loadData = function () {
		console.log("start loadData...");
	    document.getElementById('username').innerText = keycloak.subject;
	
	    const url = 'http://10.101.238.65:80/auth/restful-service';
	
	    const req = new XMLHttpRequest();
	    req.open('GET', url, true);
	    req.setRequestHeader('Accept', 'application/json');
	    req.setRequestHeader('Authorization', 'Bearer ' + keycloak.token);
	
	    req.onreadystatechange = function () {
	        if (req.readyState === 4) {
	            if (req.status === 200) {
	                alert('Success');
	            } else if (req.status === 403) {
	                alert('Forbidden');
	            }
	        }
	    }
	
	    req.send();
	};

	// When Keycloak exist (returned object)
	if (keycloak) {
		// When user is authenticated
		if (authenticated) {
			return (
				/* JSX returns an image and text as protected **resources** */
				<div className='my-12 grid place-items-center' >
					<p> You are logged in.</p>
				</div >
			)
		}
		else return (<div className='my-12'>Unable to initiate auth!</div>)
	}

	return (
		<>
			<div className='my-12'>Keycloak initializing in a moment...</div>
		</>
	)
};
