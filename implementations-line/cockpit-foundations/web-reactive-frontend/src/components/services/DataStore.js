import {combineReducers, configureStore} from '@reduxjs/toolkit'
import accountTenantRegistrationReducer from '../../features/access-control/AccountRegistrationContainer'
import infoconCockpitReducer from '../../features/cockpits/InfoconCockpitScreenContainer'
import perspectivesCockpitReducer from '../../features/cockpits/CockpitPerspectivesContainer'
import userContextReducer from '../../features/access-control/UserContextContainer'
import {persistReducer, persistStore} from 'redux-persist';
//import storage from 'redux-persist/lib/storage'; // defaults to localStorage for web
import storage from 'redux-persist/lib/storage/session';
import {REGISTER} from "redux-persist/es/constants";

// Define transversal persistence (e.g between opened browsers and panels) of application
// regarding the shared datastore state
const persistConfig = {
    key: 'root',
    storage,
}

// Define set of reducers which require to be in a persistence capability
const rootReducer = combineReducers({
    // --- Cockpits feature data perimeter ---
    // Current perspectives of information managed by cockpit sub-screens
    managedPerspectives: perspectivesCockpitReducer,
    // User context
    userContext: userContextReducer,
    // Current organization infocon level
    infoconLevel: infoconCockpitReducer,

    // --- Access-Control feature data perimeter ---
    // Tenant name reservation data
    tenantReservation: accountTenantRegistrationReducer,
})

const persistedReducer = persistReducer(persistConfig, rootReducer);

export const store = configureStore({
    // Create an empty local Redux store with automatic configuration of Redux DevTools extension allowing to inspect it while developing
    // and add reducer function from each container component to the DataStore THAT DOES NOT REQUIRE PERSISTENCE.
    // By defining a field inside the reducer parameter, the store use the slice reducer function to handle all updates to that state.
    reducer: persistedReducer,
    // Activate development tools only for environments other than production
    devTools: process.env.NODE_ENV !== 'production',

    // Customized default middleware about specific behaviors without removing all other middlewares
    middleware: (getDefaultMiddleware) => getDefaultMiddleware({
        immutableCheck: {
            // Customize behavior about immutability state (redux-immutable-state-invariant)
            // avoiding default thrown errors relative to any detected mutations
            // For more details https://redux-toolkit.js.org/api/immutabilityMiddleware
            ignoredPaths: ['ignoredPath', 'ignoredNested.one', 'ignoredNested.two'],
        },
        serializableCheck: {
            // A custom middleware that detects if any non-serializable values have been included in state or dispatched actions, modeled after redux-immutable-state-invariant
            // For more details https://redux-toolkit.js.org/api/serializabilityMiddleware
            ignoreActions: [REGISTER],
        },
    }),
})

export const persistor = persistStore(store);