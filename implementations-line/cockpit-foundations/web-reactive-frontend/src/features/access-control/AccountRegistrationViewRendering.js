/**
 * Stateless function that push events (as Redux actions) to the store regarding the registration of a new organization name as tenant.
 */
import React from 'react'
import {useSelector, useDispatch} from 'react-redux'
import {
    tenantRegistrationRequested,
    tenantRegistrationNameChanged,
    tenantConformityRuleDescription
} from './AccountRegistrationContainer'
import {Button, Form} from 'react-bootstrap/'

export default function AccountRegistrationViewRendering() {
    const isSubmittable = useSelector((state) => state.tenantReservation.inputConformity);
    const dispatch = useDispatch();
    return (
        <>
            <h1>User account registration UI</h1>
            <React.Fragment>
                <Form inputMode="text">
                    <Form.Group>
                        <Form.Label>Define an organization name to register</Form.Label>
                        <Form.Control type="text" placeholder="Enter your organization name" required={true}
                                      onChange={(event) => dispatch(tenantRegistrationNameChanged(event.target.value))}/>
                        <Form.Text>
                            Unique label define an ISMS platform context for your organization ;)
                        </Form.Text>
                    </Form.Group>
                    {(isSubmittable) ? <Button as="a" size={'lg'} variant={'primary'}
                                               onClick={() => dispatch(tenantRegistrationRequested())}>Make
                            Reservation</Button>
                        :
                        <Form.Text>{tenantConformityRuleDescription}</Form.Text>}
                </Form>
            </React.Fragment>
        </>
    );

};
