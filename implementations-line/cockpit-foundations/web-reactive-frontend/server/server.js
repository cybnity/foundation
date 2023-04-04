// Instantiation of a Express web server
const express = require('express');
const app = express();
const port = process.env.PORT || 80;

/* Create session middleware
const session = require('express-session');*/

/* Configure cookie secure (Set-Cookie) for Keycloak integration
app.set('trust proxy', 1) // trust first proxy
app.use(session({
  secret: 'keyboard cat',
  resave: false,
  saveUninitialized: true,
  cookie: { secure: true }
}))*/

// Use secure cookies in production, but allowing for testing in development based on NODE_ENV in express
// See doc at https://www.npmjs.com/package/express-session#cookiesamesite
/*const sessionConfig = {
  secret: 'MYSECRET',
  name: 'appName',
  resave: false,
  saveUninitialized: false,
  store: store,
  cookie : {
    sameSite: 'strict', // THIS is the config you are looking for.
  }
};

if (process.env.NODE_ENV === 'production') {
  app.set('trust proxy', 1); // trust first proxy
  sessionConfig.cookie.secure = true; // serve secure cookies
}

app.use(session(sessionConfig));*/
// ------------

app.use((req, res, next) => {
  console.log('Time: ', Date.now());
  next();
});

app.use('/request-type', (req, res, next) => {
  console.log('Request type: ', req.method);
  next();
});

app.get('/', (req, res) => {
  res.send('Successful response.');
  // Allow forward of Keycloak cookie (AUTH_SESSION_ID_LEGACY cookie used during rediction after login success to login-actions/authenticate keycloak page) to browser that ahev default protection to tracking systems
  //res.cookie('cookieName', 'cookieValue', { sameSite: 'none', secure: true})
});

// This displays message that the server running and listening to specified port
app.listen(port, () => console.log(`Web server is listening on http port ${port}`));

// create a GET route regarding health status
app.get('/healthcheck', (req, res) => {
  res.send({ express: 'Frontend web server is running in operational state for React' });
});
