import {configureStore} from '@reduxjs/toolkit'
import accountRegistrationNameReducer from '../../features/access-control/AccountRegistrationContainer'

export default configureStore({
    // Create an empty local Redux store with automatic configuration of Redux DevTools extension allowing to inspect it while developing
    // and aa reducer function from each container component to the DataStore.
    // By defining a filed inside the reducer parameter, the store use the slice reducer function to handle all updates to that state.
    reducer: {
        tenantName: accountRegistrationNameReducer,
    },
})