/**
 * Stateful component managing the standard cockpit composite screen's state changes via reducer() capability and including the behavior logical of the global screen.
 * It's a listener of presentational components' actions published into the store.
 */
import {createSlice} from '@reduxjs/toolkit';

export const CockpitContainer = createSlice(
    {
        name: 'userAccount',
        initialState: {
            roles: 1,
        },
        reducers: {},
    });

// Action creators are generated for each case reducer function
export const {} = CockpitContainer.actions;

export default CockpitContainer.reducer;
