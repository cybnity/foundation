import * as React from 'react';
import logo from './logo.svg';
import './App.css';
import { Button, Accordion, Form } from 'react-bootstrap/';

// KEYCLOAK integration ------ See https://github.com/react-keycloak/react-keycloak/blob/master/packages/web/README.md
import { useKeycloak } from '@react-keycloak/web'
// -------

const Create = (props) => {
    const [actionNameTerm, setActionName] = React.useState('');
    const [actionDescTerm, setActionDesc] = React.useState('');
    const [existingActionDescription, setIsEmpty] = React.useState('');

    const handleNameChange = (event) => {
      setActionName(event.target.value);
    };

    const handleDescriptionChange = (event) => {
      setActionDesc(event.target.value);
      if (event.target.value === '') {
       	setIsEmpty('');
      } else {
       	setIsEmpty('including description');
      }
    };

    function generateUUIDUsingMathRandom() {
      var d = new Date().getTime();// Timestamp
      var d2 = (performance && performance.now && (performance.now()*1000)) || 0;//Time in microseconds since page-load or 0 if unsupported
      return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
          var r = Math.random() * 16;//random number between 0 and 16
          if(d > 0){//Use timestamp until depleted
              r = (d + r)%16 | 0;
              d = Math.floor(d/16);
          } else {//Use microseconds since page-load if supported
              r = (d2 + r)%16 | 0;
              d2 = Math.floor(d2/16);
          }
          return (c === 'x' ? r : (r & (0x3 | 0x8))).toString(16);
      });
    }

    function handleCreateAction() {
      var jsonBody = {
        occurredOn: '',
        correlationId: generateUUIDUsingMathRandom(),
        id: generateUUIDUsingMathRandom(),
        type: 'CommandEvent',
        name: 'createAction',
        authenticationCredential: {
          accessType: 'Bearer',
          attributes: {
            accessToken: props.onReadAccessToken().toString()
          }
        },
        inParameters: {
          domain: 'acsc',
          name: actionNameTerm,
          type: 'action',
          description: actionDescTerm
        }
      };
      // Send to event busbroker
      props.onCommit('aap.in', jsonBody);
    }

    return (
      <React.Fragment>
        <Form>
          <Form.Group className="mb-3">
            <Form.Label>Mission action name</Form.Label>
            <Form.Control type="text" placeholder="Enter an action label" id="action_name" onChange={handleNameChange}/>
            <Form.Text className="text-muted">
              Unique label to logically simplify the future search of the mission action ;)
            </Form.Text>
          </Form.Group>

          <Form.Group className="mb-3" controlId="action_description">
            <Form.Label>Mission action description</Form.Label>
            <Form.Control type="text" placeholder="Give a short presentation" onChange={handleDescriptionChange}/>
          </Form.Group>
          <Button as="a" variant="primary" onClick={handleCreateAction}>
            VALIDATE CREATION
          </Button>
        </Form>
        <Form.Text className="text-muted">Ready to commit the creation of a new mission action named {actionNameTerm} {existingActionDescription}</Form.Text>
      </React.Fragment>
    );
}

const Secured = (props) => {
  //const initialAssets = React.useState([]);
  //const findAsyncAssets = () => Promise.resolve({data: {assets: initialAssets}});

  //const [assets, setAssets] = React.useState([]);

  //React.useEffect(() => {
    // Read data asynchronouslt from backend server
    //findAsyncAssets().then(result => {
      //setAssets(result.data.assets);
    //});
  //},[]);

  const { keycloak, setKeycloak } = useKeycloak();
  // Here you can access all of keycloak methods and variables.
  // See https://www.keycloak.org/docs/latest/securing_apps/index.html#javascript-adapter-reference
  // https://github.com/react-keycloak/react-keycloak/blob/master/packages/web/README.md
  function getTitle(title) {
      return title;
  }

  const handleCreate = (channel, msg)  => {
    props.onEvent(channel, msg);
  };

  function getAccessToken() {
    return props.getAccessToken();
  }

  return (
      <div className="App">
        {!!keycloak.authenticated && (
            <header className="App-header">
              <h1>Welcome, in a protected {getTitle('React')} world!</h1>
              <Accordion defaultActiveKey="0">
                <Accordion.Item eventKey="0">
                  <Accordion.Header>How to create a new mission action?</Accordion.Header>
                  <Accordion.Body><Create onCommit={handleCreate} onReadAccessToken={getAccessToken}/></Accordion.Body>
                </Accordion.Item>
                <Accordion.Item eventKey="1">
                  <Accordion.Header>ReactJS, what is it?</Accordion.Header>
                  <Accordion.Body><p>
                  <img valign="middle" src={logo} className="App-logo" alt="logo" width="100px"/>Edit <code>src/App.js</code> and save to reload.
                  </p>
                  <a
                    className="App-link"
                    href="https://reactjs.org"
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    Learn React
                  </a></Accordion.Body>
                </Accordion.Item>
              </Accordion>
            </header>
        )}
      </div>
  );
}

export default Secured;
