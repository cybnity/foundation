import {configureStore} from '@reduxjs/toolkit'
import accountTenantRegistrationReducer from '../../features/access-control/AccountRegistrationContainer'
import infoconCockpitReducer from '../../features/cockpits/InfoconCockpitScreenContainer'
import perspectivesCockpitReducer from '../../features/cockpits/CockpitPerspectivesContainer'
import userContextReducer from '../../features/access-control/UserContextContainer'

export default configureStore({
    // Create an empty local Redux store with automatic configuration of Redux DevTools extension allowing to inspect it while developing
    // and aa reducer function from each container component to the DataStore.
    // By defining a field inside the reducer parameter, the store use the slice reducer function to handle all updates to that state.
    reducer: {
        // --- Access-Control feature data perimeter ---
        // Tenant name reservation data
        tenantReservation: accountTenantRegistrationReducer,

        // --- Cockpits feature data perimeter ---
        // Current perspectives of information managed by cockpit sub-screens
        managedPerspectives: perspectivesCockpitReducer,
        // User context
        userContext: userContextReducer,

        // Current organization infocon level
        infoconLevel: infoconCockpitReducer,
    }
})