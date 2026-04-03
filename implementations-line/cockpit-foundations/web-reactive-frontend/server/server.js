const cors = require('cors');
/* Create session middleware (see https://www.npmjs.com/package/express-session#cookiesamesite ) */
const session = require('express-session');
// Instantiation of a Express web server
const express = require('express');
// Add connector to Keycloak from express
const Keycloak = require('keycloak-connect');
const app = express();

const port = process.env.PORT || 80;
const serverOrigin = "http://localhost:" + port;

function authServerURL() {
  // Read eventually defined environment variable regarding URL of Keycloak server
  if (process.env.AUTH_SERVER_URL) return process.env.AUTH_SERVER_URL;
  // Return default localhost url
  return serverOrigin + "/auth/";
}

app.use(cors({
    origin: serverOrigin, // use your actual domain name (or localhost), using * is not recommended
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'HEAD', 'OPTIONS'],
    allowedHeaders: ['Content-Type', 'Origin', 'X-Requested-With', 'Accept', 'x-client-key', 'x-client-token', 'x-client-secret', 'Authorization', 'Access-Control-Allow-Origin'],
    credentials: true
})); // Enable CORS and allow (ACL headers add in responses)

// Add role-based access for frontend server-side calls
const router = express.Router();

// Use secure cookies in production, but allowing for testing in development based on NODE_ENV in express
if (process.env.DEPLOYMENT === 'production') {
  /* Configure cookie secure (Set-Cookie) for Keycloak integration.
  Please note that secure: true is a recommended option. However, it requires an https-enabled website, i.e., HTTPS is necessary for secure cookies. If secure is set, and you access your site over HTTP, the cookie will not be set. If you have your node.js behind a proxy and are using secure: true, you need to set "trust proxy" in express */
  app.set('trust proxy', true); // Set trust proxy in express
  // See doc at https://www.npmjs.com/package/express-session#cookiesamesite
  //sessionConfig.cookie.secure = true; // serve secure cookies
  app.use(session({
    secret: 'memory store SECRET',
    // The name of the session ID cookie to set in the response (and read from in the request). default value is 'connect.sid'
    // name: 'web-reactive-frontend-system',
    /* Trust the reverse proxy when setting secure cookies (via the "X-Forwarded-Proto" header)
       undefined Uses the "trust proxy" setting from express */
    // proxy: 'undefined'
    /* Forces the session to be saved back to the session store, even if the session was never modified during the request. Depending on your store this may be necessary, but it can also create race conditions where a client makes two parallel requests to your server and changes made to the session in one request may get overwritten when the other request ends, even if it made no changes (this behavior also depends on what store you're using) */
    resave: false,
    /* Forces a session that is "uninitialized" to be saved to the store. A session is uninitialized when it is new but not modified */
    saveUninitialized: false,
    cookie : {
      /* Value for the SameSite Set-Cookie attribute (default is false). 'true' or 'strict' will set the SameSite attribute to Strict for strict same site enforcement. See https://tools.ietf.org/html/draft-ietf-httpbis-rfc6265bis-03#section-4.1.2.7 for more information about the enforcement levels */
      sameSite: 'strict',
      /* when setting this to true, as compliant clients will not send the cookie back to the server in the future if the browser does not have an HTTPS connection. HTTPS is necessary for secure cookies */
      secure: true // Server secure cookies
    }
  })); // using secure cookie in production environment
} else {
  app.use(session({
    // Default unsecure cookie allowing for testing in development
    secret: 'keyboard cat',
    resave: false,
    /* Choosing false is useful for implementing login sessions, reducing server storage usage, or complying with laws that require permission before setting a cookie. Choosing false will also help with race conditions where a client makes multiple parallel requests without a session */
    saveUninitialized: false,
    cookie: {
      /* 'none' will set the SameSite attribute to None for an explicit cross-site cookie. */
      sameSite: 'none',
      secure: false
    }
  })); // using unsecure cookie in testing environment
}

const memoryStore = new session.MemoryStore();

var keycloakConfig = {
  "realm": "CYBNITY",
  "auth-server-url": authServerURL(),
  "ssl-required": "external",
  "resource": "web-reactive-frontend-system",
  "public-client": true,
  "verify-token-audience": true,
  "use-resource-role-mappings": true,
  "confidential-port": 0
};

// See tutorial for help at https://medium.com/devops-dudes/secure-front-end-react-js-and-back-end-node-js-express-rest-api-with-keycloak-daf159f0a94e
const keycloak = new Keycloak({
            store: memoryStore,
            secret: "a secret key",
            resave: false,
            saveUninitialized: true
        }, keycloakConfig);

// ------------

// Link routing via Keycloak RBAC
app.use(keycloak.middleware());

app.use((req, res, next) => {
  // CORS policy : allow routing to Keycloak protected resources securized regarding this frontend route
  res.header('Access-Control-Allow-Origin', req.header('Origin'));
  res.header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE');
  res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept');
  res.header('Access-Control-Allow-Credentials', true);
  res.header("Content-Security-Policy", "frame-ancestors 'self'"); // add CSP (Content Security Policy) indicating to browser that page shall not be shown into a frame if the frame is not owned by the same domain

  console.log('Time: ', Date.now());
  //console.log("ACL headers regarding " + serverOrigin + " host have been defined in response");
  next();
});

app.use('/request-type', (req, res, next) => {
  console.log('Request type: ', req.method);
  next();
});

app.get('/', (req, res) => {
  res.send('Server is up collaborating with authentication server (at ' + authServerURL() + ')');
  // Allow forward of Keycloak cookie (AUTH_SESSION_ID_LEGACY cookie used during rediction after login success to login-actions/authenticate keycloak page) to browser that ahev default protection to tracking systems
  //res.cookie('cookieName', 'cookieValue', { sameSite: 'none', secure: true})
});

// create a GET route regarding health status
app.get('/healthcheck', (req, res) => {
  res.send({ express: 'Frontend web server is running in operational state for React' });
});

// ---- ROUTING TO FRONTEND SECURIZED RESOURCES (see https://www.keycloak.org/docs/latest/securing_apps/#_nodejs_adapter) ---
app.get('/secure', keycloak.protect(), function(req, res) {
  // Enforce that a user shall be authenticated before accessing a resource

  // Allow forward of Keycloak cookie (AUTH_SESSION_ID_LEGACY cookie used during rediction after login success to login-actions/authenticate keycloak page) to browser that have default protection to tracking systems
  //res.cookie('cookieName', 'cookieValue', { sameSite: 'none', secure: true});

  res.send({ express: 'Securized access by Keycloak' });
});

app.get('/secure/tenant', keycloak.protect('tenant-user'), function(req, res) {
  // Role-based authorized resource

  res.send({ express: 'Securized resource accessed' });
});

// This displays message that the server running and listening to specified port
app.listen(port, () => console.log('Web server is listening on http port ' + port));

