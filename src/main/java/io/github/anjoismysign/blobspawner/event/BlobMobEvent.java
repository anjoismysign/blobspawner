package io.github.anjoismysign.blobspawner.event;

import io.github.anjoismysign.blobspawner.domain.BlobMob;
import org.bukkit.event.Event;

public abstract class BlobMobEvent extends Event {

    private final BlobMob blobMob;

    public BlobMobEvent(boolean isAsync,
                        BlobMob blobMob) {
        super(isAsync);
        this.blobMob = blobMob;
    }

    public BlobMob getBlobMob() {
        return blobMob;
    }
}
