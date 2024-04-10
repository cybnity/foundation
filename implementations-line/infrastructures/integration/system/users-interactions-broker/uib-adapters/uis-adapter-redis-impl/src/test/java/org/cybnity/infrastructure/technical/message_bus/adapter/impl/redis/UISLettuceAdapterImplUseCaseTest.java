package org.cybnity.infrastructure.technical.message_bus.adapter.impl.redis;

import org.cybnity.infrastructure.technical.message_bus.adapter.api.UISAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.logging.Logger;

/**
 * Test and check the instantiation of the adapter implementation reusing the Lettuce library and settings.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UISLettuceAdapterImplUseCaseTest extends ContextualizedRedisActiveTestContainer {

    private final Logger logger = Logger.getLogger(UISLettuceAdapterImplUseCaseTest.class.getName());

    /**
     * This test is creating an adapter instance and validate its operational usage according to the current configuration.
     */
    @Test
    public void givenRedisSpaceStarted_whenClientConfigurationDefined_thenAdapterInstantiatedWithSuccess() throws Exception {
        // Try adapter instance creation with automatic configuration of Lettuce client
        // from a valid context settings set
        UISAdapter adapter = new UISAdapterRedisImpl(getContext());
        adapter.freeUpResources();
    }

}
