/**
 * Stateful component managing the user context changes via reducer() capability.
 */
import {createSlice} from '@reduxjs/toolkit';

export const UserContextContainer = createSlice(
    {
        name: 'userContext',
        initialState: {
            isAuthenticated: false,
            ssoToken: ''
        },
        reducers: {}
    });

// Action creators are generated for each case reducer function
export const {} = UserContextContainer.actions;

export default UserContextContainer.reducer;
