package io.github.anjoismysign.blobspawner.event;

import io.github.anjoismysign.blobspawner.domain.BlobMob;
import io.github.anjoismysign.blobspawner.domain.BlobMobSpawner;
import org.bukkit.event.HandlerList;

public class BlobMobSpawnEvent extends BlobMobEvent {
    private final BlobMobSpawner spawner;

    private static final HandlerList handlers = new HandlerList();

    public BlobMobSpawnEvent(BlobMob blobMob,
                             BlobMobSpawner spawner) {
        super(false, blobMob);
        this.spawner = spawner;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public BlobMobSpawner getSpawner() {
        return spawner;
    }
}
