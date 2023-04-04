import Keycloak from "keycloak-js";

const keycloak = Keycloak('/keycloak.json');

function initKeycloak() {
	keycloak.init({
		onLoad: 'login-required',
		checkLoginIframe: false
	}).then(authenticated => { alert(authenticated ? 'authenticated' : 'not authenticated'); })
		.catch(error => { console.log(error) })
}
export default keycloak;
