// --------- VERTX EVENT BUS INTEGRATION -------
// See https://www.npmjs.com/package/@vertx/eventbus-bridge-client.js
import EventBus from '@vertx/eventbus-bridge-client.js';

const busOptions = {
    vertxbus_reconnect_attempts_max: Infinity, // Max reconnect attempts
    vertxbus_reconnect_delay_min: 1000, // Initial delay (in ms) before first reconnect attempt
    vertxbus_reconnect_delay_max: 5000, // Max delay (in ms) between reconnect attempts
    vertxbus_reconnect_exponent: 2, // Exponential backoff factor
    vertxbus_randomization_factor: 0.5 // Randomization factor between 0 and 1
};

// Define server-side url to messaging gateway
const backendUrl = 'http://localhost:8080/eventbus/secure/';

// Connect Backend SockJS api via event bus
let eventBus = new EventBus(backendUrl, busOptions);
export default eventBus;

// Set up event bus handlers...
eventBus.onopen = function () {
    console.log("Event bus opened");
    eventBus.enableReconnect(true);
}

eventBus.onreconnect = function () {
    console.log("Event bus reconnected");
}; // Optional, will only be called on re-connections

eventBus.onerror = function (error) {
    console.log("Event bus error: " + JSON.stringify(error));
}