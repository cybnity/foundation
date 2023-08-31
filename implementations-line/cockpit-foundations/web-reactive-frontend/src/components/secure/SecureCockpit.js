import React from 'react';
import AccessControlCheck from "./AccessControlCheck";
import gatewayProxy from "./../services/GatewayProxy";

const [tenantId, setTenantId] = React.useState('');// Tenant of this cockpit runtime context
const [connectedUserId, setConnectedUserId] = React.useState('');// Identifier of authenticated user
const [organizationBusEntryPoint, setOrganizationBusEntryPoint] = React.useState(tenantId + '.');

function registerHandlers() {
    // --- COCKPIT HANDLERS REGISTRATION about common listened events ---
    // Set a handler to receive UI capabilities answers over the event bus
    // Connected user dedicated channel handler
    gatewayProxy.registerHandler(tenantId + '.ac.out', function (error, message) {
        console.log(`received a message from ${tenantId}.ac.out: ${JSON.stringify(message)}`);
    });
    // User organization dedicated channel handler
    gatewayProxy.registerHandler(tenantId + '.out', function (error, message) {
        console.log(`received a message from ${tenantId}.out: ${JSON.stringify(message)}`);
    });
}

export default function SecureCockpit() {

    return (
        <div className="SecureCockpit" onLoad={registerHandlers}>
            <p>This is your keycloak authenticated-facing cockpit canvas</p>
        </div>
    );
};