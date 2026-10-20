package io.github.anjoismysign.blobspawner.configuration;

import io.github.anjoismysign.blobspawner.BlobSpawner;

public class SpawnerConfiguration {
    public static SpawnerConfiguration getInstance(){
        return BlobSpawner.getInstance().getManagerDirector().getConfigurationManager().getConfiguration();
    }

    private boolean tinyDebug;

    public boolean isTinyDebug() {
        return tinyDebug;
    }

    public void setTinyDebug(boolean tinyDebug) {
        this.tinyDebug = tinyDebug;
    }

}
