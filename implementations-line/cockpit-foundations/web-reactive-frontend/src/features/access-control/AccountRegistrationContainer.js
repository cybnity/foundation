/**
 * Statefull component managing the account registration screen's state changes via reducer() capability and including the behavior logical of the screen.
 * It's a listener of presentational components' actions published into the store.
 */
import {createSlice} from '@reduxjs/toolkit';

export const AccountRegistrationContainer = createSlice(
    {
        name: 'tenantName',
        initialState: {
            value: '',
        },
        reducers: {
            validate: (state) => {
                // "mutating" logic that doesn't actually mutate the state because this reducer use the Immer library,
                // which detects changes to a "draft state" and produces a brand new immutable state based off thoses changes.
                // Also, no return statement is required from these functions.
                // See Redux State Slice tutorial at https://react-redux.js.org/tutorials/quick-start#create-a-redux-state-slice

                // Verify the conformity of the tenant name value and apply acceptance rules before that name can be registered as organization tenant

            },
            change: (state, action) => {
                state.value = action.payload;
                console.log("action reduced = " +  JSON.stringify(action));
                console.log("state reduced = " +  JSON.stringify(state.value));
            },
        },
    });
// Action creators are generated for each case reducer function
export const {validate, change} = AccountRegistrationContainer.actions;

export default AccountRegistrationContainer.reducer;
