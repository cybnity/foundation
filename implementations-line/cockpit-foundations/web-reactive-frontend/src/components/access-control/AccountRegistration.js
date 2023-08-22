import * as React from 'react';
import { Button, Form } from 'react-bootstrap/';

export default function AccountRegistration() {
  const [tenantName, setTenantName] = React.useState('');
  const handleNameChange = (event) => {
    setTenantName(event.target.value);
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

  function handleRegisterTenant() {
    var jsonBody = {
      occurredOn: '',
      correlationId: generateUUIDUsingMathRandom(),
      id: generateUUIDUsingMathRandom(),
      type: 'CommandEvent',
      name: 'registerTenant',
      authenticationCredential: {
        accessType: 'Bearer',
        attributes: {
          //accessToken: props.onReadAccessToken().toString()
        }
      },
      inParameters: {
        domain: 'asset_control',
        name: tenantName
      }
    };
    console.log("tenant send: " + tenantName);
  }

  return (
    <div className="p-3 mb-2 bg-secondary text-white">
      <p> </p>
      <h2>User account registration UI</h2>

      <React.Fragment>
        <Form>
          <Form.Group className="mb-3">
            <Form.Label>Define your account name to create</Form.Label>
            <Form.Control type="text" placeholder="Enter your organization name" id="organization_name" onChange={handleNameChange}/>
            <Form.Text className="text-muted">
              Unique label define your your ISMS platform context ;)
            </Form.Text>
          </Form.Group>

          <Button as="a" variant="info" onClick={handleRegisterTenant}>Register</Button>
        </Form>
      </React.Fragment>
    </div>
  );

};
