package io.github.anjoismysign.blobspawner.configuration;

public class SpawnerConfiguration {
    private boolean tinyDebug;

    SpawnerConfiguration(){}

    public boolean isTinyDebug() {
        return tinyDebug;
    }

    public void setTinyDebug(boolean tinyDebug) {
        this.tinyDebug = tinyDebug;
    }
}
