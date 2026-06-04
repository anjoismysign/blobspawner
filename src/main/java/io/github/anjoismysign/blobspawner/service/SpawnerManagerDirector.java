package io.github.anjoismysign.blobspawner.service;

import io.github.anjoismysign.bloblib.entities.GenericManagerDirector;
import io.github.anjoismysign.blobspawner.BlobSpawner;
import org.jetbrains.annotations.NotNull;

public class SpawnerManagerDirector extends GenericManagerDirector<BlobSpawner> {
    public SpawnerManagerDirector(BlobSpawner plugin) {
        super(plugin);
        addManager("ConfigurationManager",
                new SpawnerConfigurationManager(this));
        addManager("BlobMobManager",
                new BlobMobManager(this));
    }

    /**
     * From top to bottom, follow the order.
     */
    @Override
    public void reload() {
        getConfigurationManager().reload();
        getBlobMobManager().reload();
    }

    @Override
    public void unload() {
    }

    @NotNull
    public final SpawnerConfigurationManager getConfigurationManager() {
        return getManager("ConfigurationManager", SpawnerConfigurationManager.class);
    }

    @NotNull
    public final BlobMobManager getBlobMobManager() {
        return getManager("BlobMobManager", BlobMobManager.class);
    }
}
