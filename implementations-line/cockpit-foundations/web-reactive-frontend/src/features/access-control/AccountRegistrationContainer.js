/**
 * Stateful component managing the account registration screen's state changes via reducer() capability and including the behavior logical of the screen.
 * It's a listener of presentational components' actions published into the store.
 */
import {createSlice} from '@reduxjs/toolkit';

function generateUUIDUsingMathRandom() {
    let d = new Date().getTime();// Timestamp
    let d2 = (performance && performance.now && (performance.now() * 1000)) || 0;//Time in microseconds since page-load or 0 if unsupported
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        let r = Math.random() * 16;//random number between 0 and 16
        if (d > 0) {//Use timestamp until depleted
            r = (d + r) % 16 | 0;
            d = Math.floor(d / 16);
        } else {//Use microseconds since page-load if supported
            r = (d2 + r) % 16 | 0;
            d2 = Math.floor(d2 / 16);
        }
        return (c === 'x' ? r : (r & (0x3 | 0x8))).toString(16);
    });
}

const tenantConformityPattern = "([a-zA-Z0-9]_*?){6,}"; // including minimum 6 characters, digits and or underscore without space
export const tenantConformityRuleDescription = "A valid name shall include minimum 6 characters, digit and/or underscore without space."

/**
 * Conformity of the tenant name value is verified over acceptance rules.
 * @param label
 */
function checkTenantNameConformity(label) {
    let regex = new RegExp(tenantConformityPattern);
    return !!label.match(regex);
}

/**
 * Submit the organization name for registration as tenant to the backend capability.
 * @param tenantName
 */
function createTenantReservation(tenantName) {
    if (tenantName) {
        let jsonBody = {
            occurredOn: new Date().getTime(),
            correlationId: generateUUIDUsingMathRandom(),
            id: generateUUIDUsingMathRandom(),
            type: 'CommandEvent',
            name: 'reserveTenant',
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
        console.log("Build tenant name reservation message: " + JSON.stringify(jsonBody));
    }
}

export const AccountRegistrationContainer = createSlice(
    {
        name: 'tenantReservation',
        initialState: {
            value: '',
            inputConformity: false,
        },
        reducers: {
            tenantRegistrationNameChanged: (state, action) => {
                // "mutating" logic that doesn't actually mutate the state because this reducer use the Immer library,
                // which detects changes to a "draft state" and produces a brand new immutable state based off those changes.
                state.value = action.payload;

                // Check conformity to allow or refuse current registrable status
                state.inputConformity = checkTenantNameConformity(state.value);

                console.log("action (type: " + action.type + "), state current value:" + state.value + ', current validity:' + state.inputConformity);
                // Also, no return statement is required from these functions.
                // See Redux State Slice tutorial at https://react-redux.js.org/tutorials/quick-start#create-a-redux-state-slice
            },
            tenantRegistrationRequested: (state) => {
                if (state.inputConformity) {
                    // Submit the organization name for registration as tenant
                    createTenantReservation(state.value);
                } else {
                    console.log("Tenant name reservation message not send for cause of organization name non conformity!");
                }
            },
        },
    });
// Action creators are generated for each case reducer function
export const {tenantRegistrationRequested, tenantRegistrationNameChanged} = AccountRegistrationContainer.actions;

export default AccountRegistrationContainer.reducer;
