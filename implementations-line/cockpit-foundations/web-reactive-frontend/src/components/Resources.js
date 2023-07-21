import { useState, useEffect } from 'react';
import Keycloak from 'keycloak-js';

export default function Resources() {
	// Check the keycloak instance and authentication
	const [keycloak, setKeycloak] = useState(null)
	const [authenticated, setAuthenticated] = useState(false)

	// Track the render cycle when the component is mounted onto the DOM to invoke the Keycloak instance
	useEffect(() => {
		const keycloak = Keycloak('/keycloak.json');
		keycloak.init({ onLoad: 'login-required', checkLoginIframe: true, silentCheckSsoFallback: false }).then(authenticated => {
			setKeycloak(keycloak);
			console.log("IsAuthenticated: " + authenticated);
			if (authenticated) {
				// Store the access token received from authenticated Keycloak object to call back-end APIs
				window.accessToken = keycloak.token;
				// See https://jwt.io/ web page to read the token in plain text
				// Read JWT token
				console.log("Access token: " + keycloak.token);
				console.log("Username: " + keycloak.tokenParsed?.preferred_username);
				console.log("Keycloak user account ID: " + keycloak.tokenParsed?.sub);
				console.log("Profile identified as: " + keycloak.tokenParsed?.given_name + " " + keycloak.tokenParsed?.family_name + " (" + keycloak.tokenParsed?.name + ")");
				console.log("Email: " + keycloak.tokenParsed?.email);
				console.log("Realm Access roles: " + keycloak.tokenParsed?.realm_access.roles);
				console.log("Realm roles: " + keycloak.tokenParsed?.realm.role);
				console.log("Account attributes - locale: " + keycloak.tokenParsed?.locale);

				// Test the loading of any data supported by a sso check
				/*console.log("Test data load...");
				const url = '/secure';// 'http://10.101.238.65:80/api/';

				const req = new XMLHttpRequest();
				req.open('GET', url, true);
				req.setRequestHeader('Accept', 'application/json');
				req.setRequestHeader('Authorization', 'Bearer ' + (window.accessToken ? window.accessToken : 'dummy_token'));

				req.onreadystatechange = function () {
					if (req.readyState === 4) {
						if (req.status === 200) {
							console.log('Data load with success');
							// Show response received from the Frontend server
							console.log(req.response);
						} else if (req.status === 403) {
							alert('Data load is forbidden!');
						} else if (req.status === 503) {
							alert('Reactive Backend System 503 response!');
						}
					}
				}

				req.send();*/
			}
			setAuthenticated(authenticated);
		})
	}, [])



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
