/**
 * Stateful component managing the information screens (e.g opened as tabs and managing dedicated information perspectives) state changes via reducer() capability and including the behavior logical of the perspectives activation/deletion.
 */
import {createSlice} from '@reduxjs/toolkit';

/**
 * Search existing equals perspective already managed by the perspectives data set.
 * @param array Referential elements list to read.
 * @param perspectiveId Mandatory identifier of perspective to search.
 * @returns {boolean} False by default. True when equals identified perspective is found in current data set.
 */
function isManagedPerspective(array, perspectiveId) {
    let found = false;
    if (perspectiveId) {
        let foundManagedPerspective = search(array, perspectiveId, "id");
        if (foundManagedPerspective) found = true;
    }
    return found;
}

/**
 * Represents a search of element through an array.
 * @function search
 * @param {Array} array - The array wanted to be searched trough
 * @param {string} key - The key to search for
 * @param {string} [prop] - The property name to find it in
 */
function search(array, key, prop) {
    // Optional, but fallback to key['id'] if not selected
    prop = (typeof prop === 'undefined') ? 'id' : prop;
    for (let i = 0; i < array.length; i++) {
        if (array[i][prop] === key) {
            return array[i];
        }
    }
}

function findLast(array) {
    return (array.length > 0) ? array[array.length - 1] : array[array.length];
}

/**
 * Find index of an element.
 * @param array
 * @param key
 * @param prop
 * @returns {number} Found index.
 */
function indexOf(array, key, prop) {
    // Optional, but fallback to key['id'] if not selected
    prop = (typeof prop === 'undefined') ? 'id' : prop;
    for (let i = 0; i < array.length; i++) {
        if (array[i][prop] === key) {
            return i;
        }
    }
}

export const CockpitPerspectivesContainer = createSlice(
    {
        name: 'managedPerspectives',
        initialState: {
            set: [
                { // Main cockpit ISMS kpi dashboard (not closable) regarding the global cockpit
                    id: 'control', // UUID of information screen
                    title: 'CONTROL', // Title of the information screen
                    componentName: 'SituationPanelScreenDisplay',
                    closable: false,
                    exportable: false,
                    imageMode: false,
                    titleImage: ''
                },
                // Other managed perspectives descriptions
                // are dynamically added in the array by view containers
            ],
            activePerspective: 'control', // Current perspective focused
        },
        reducers: {
            perspectiveOpened: (state, action) => {
                // Check that equals identified perspective is not already contained by the perspectives list
                if (!isManagedPerspective(state.set, action.payload.id)) {
                    // Not managed perspective, that can be added into the data set
                    // Add new perspective description to the set of managed screens
                    state.set.push(action.payload);
                    console.log('Opened and managed perspective: (id: ' + action.payload.id + ')');
                } else {
                    console.log('Perspective is already managed by the data layer!');
                }
                // Change current active perspective in data store
                state.activePerspective = action.payload.id;
            },
            perspectiveActivated: (state, action) => {
                // Identify which perspective have been activated, selected as focused by user in screen
                // Change current active perspective in data store
                state.activePerspective = action.payload.perspectiveId;
                console.log("Activated perspective (action type: " + action.type + ", id: " + state.activePerspective + ")");
            },
            perspectiveClosed: (state, action) => {
                // Check that equals identified perspective is contained by the perspectives list
                if (isManagedPerspective(state.set, action.payload.id)) {
                    // Managed perspective, that can be removed from the data set
                    let toRemoveObj = state.set.find(item => item.id === action.payload.id);
                    // Verify if it's a closable perspective
                    if (toRemoveObj.closable === true) {
                        // Find index of element to remove
                        let index = indexOf(state.set, toRemoveObj.id, "id");
                        if (index > -1) {
                            // Delete the perspective description to the set of managed screens
                            state.set.splice(index, 1); // Remove only one element from perspective index

                            console.log('Removed perspective: (id: ' + action.payload.id + ')');

                            // Find previous perspective that could be eligible as new active with focus
                            let lastManagedPerspective = findLast(state.set);
                            if (lastManagedPerspective) {
                                // Reactive the previous opened perspective
                                // Change current active perspective in data store
                                state.activePerspective = lastManagedPerspective.id;
                                console.log('Previous perspective (id: ' + lastManagedPerspective.id + ') changed as current active');
                            }
                        }
                    } else {
                        console.log("Found perspective but that is not closable!");
                    }
                } else {
                    console.log('Unknown perspective to be removed from the data layer!');
                }
            }
        },
    })
;
// Action creators are generated for each case reducer function
export const {perspectiveOpened, perspectiveActivated, perspectiveClosed} = CockpitPerspectivesContainer.actions;

export default CockpitPerspectivesContainer.reducer;
