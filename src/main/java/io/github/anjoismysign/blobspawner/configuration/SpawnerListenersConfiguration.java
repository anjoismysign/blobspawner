package io.github.anjoismysign.blobspawner.configuration;

public class SpawnerListenersConfiguration {

    private boolean cancelNaturalSpawns = true;

    public boolean isCancelNaturalSpawns() {
        return cancelNaturalSpawns;
    }

    public void setCancelNaturalSpawns(boolean cancelNaturalSpawns) {
        this.cancelNaturalSpawns = cancelNaturalSpawns;
    }

}
