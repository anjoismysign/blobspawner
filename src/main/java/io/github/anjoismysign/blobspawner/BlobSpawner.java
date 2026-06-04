package io.github.anjoismysign.blobspawner;

import io.github.anjoismysign.bloblib.managers.BlobPlugin;
import io.github.anjoismysign.bloblib.managers.PluginManager;
import io.github.anjoismysign.bloblib.managers.asset.BukkitIdentityManager;
import io.github.anjoismysign.blobspawner.command.BlobMobCmd;
import io.github.anjoismysign.blobspawner.domain.BlobMobData;
import io.github.anjoismysign.blobspawner.domain.BlobMobSpawnerData;
import io.github.anjoismysign.blobspawner.service.SpawnerManagerDirector;

public final class BlobSpawner extends BlobPlugin {
    private static BlobSpawner INSTANCE;

    private SpawnerManagerDirector director;
    private BukkitIdentityManager<BlobMobData> blobMobIdentityManager;
    private BukkitIdentityManager<BlobMobSpawnerData> blobMobSpawnerIdentityManager;

    public static BlobSpawner getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        director = new SpawnerManagerDirector(this);

        PluginManager pluginManager = PluginManager.getInstance();
        blobMobIdentityManager = pluginManager.addIdentityManager(BlobMobData.Info.class, this, "mob", true);
        blobMobSpawnerIdentityManager = pluginManager.addIdentityManager(BlobMobSpawnerData.Info.class, this, "mob spawner", true);
        BlobMobCmd.INSTANCE.initialize();
    }

    public SpawnerManagerDirector getManagerDirector() {
        return director;
    }

    public BukkitIdentityManager<BlobMobData> getBlobMobIdentityManager() {
        return blobMobIdentityManager;
    }

    public BukkitIdentityManager<BlobMobSpawnerData> getBlobMobSpawnerIdentityManager() {
        return blobMobSpawnerIdentityManager;
    }
}
