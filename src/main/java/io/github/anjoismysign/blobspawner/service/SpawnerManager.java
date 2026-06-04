package io.github.anjoismysign.blobspawner.service;

import io.github.anjoismysign.bloblib.entities.GenericManager;
import io.github.anjoismysign.blobspawner.BlobSpawner;

public class SpawnerManager extends GenericManager<BlobSpawner, SpawnerManagerDirector> {
    public SpawnerManager(SpawnerManagerDirector managerDirector) {
        super(managerDirector);
    }
}
