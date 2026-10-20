package io.github.anjoismysign.blobspawner.manager;

import io.github.anjoismysign.bloblib.manager.SmartListenerManager;
import io.github.anjoismysign.blobspawner.configuration.SpawnerListenersConfiguration;
import io.github.anjoismysign.blobspawner.service.SpawnerManagerDirector;

public class SpawnerListenerManager extends SmartListenerManager<SpawnerListenersConfiguration> {

    public SpawnerListenerManager(SpawnerManagerDirector managerDirector) {
        super(managerDirector);
    }

    @Override
    public Class<SpawnerListenersConfiguration> getConfigurationClass() {
        return SpawnerListenersConfiguration.class;
    }
}
