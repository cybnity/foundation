/**
 * Stateful component managing the standard cockpit composite screen's Infocon level state changes via reducer() capability.
 * It's a listener of Infocon level components' actions published into the store (e.g allowing its upgrade).
 */
import {createSlice} from '@reduxjs/toolkit'

/**
 * Definition of reducer monitoring the Infocon level
 */
export const InfoconCockpitScreenContainer = createSlice(
    {
        name: 'infoconLevel',
        initialState: {
            level: 5,
            description: 'Operational performance monitoring cockpit',
            label: 'Level 5',
        },
        reducers: {
            infoconChanged: (state, action) => {
                let oldStatus = state.level;
                // Update local data store current level
                state.level = action.payload.level;
                console.log("current Infocon status (" + oldStatus + ") is changed to " + state.level);
            },
        },
    });

// Action creators are generated for each case reducer function
export const {infoconChanged} = InfoconCockpitScreenContainer.actions;

export default InfoconCockpitScreenContainer.reducer;