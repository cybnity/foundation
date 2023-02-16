// Instantiation of a Express web server
const express = require('express');
const app = express();
const port = process.env.PORT || 80;

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
});

// This displays message that the server running and listening to specified port
app.listen(port, () => console.log(`Web server is listening on http port ${port}`));

// create a GET route regarding health status
app.get('/healthcheck', (req, res) => {
  res.send({ express: 'Frontend web server is running in operational state for React' });
});
