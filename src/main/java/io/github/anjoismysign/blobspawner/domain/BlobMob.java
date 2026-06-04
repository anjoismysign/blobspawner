package io.github.anjoismysign.blobspawner.domain;

import kr.toxicity.model.api.tracker.EntityTracker;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BlobMob {
    private final @NotNull BlobMobSpawner spawner;
    private final @NotNull Mob mob;
    private final double spawnAsLegendaryChance;
    private final @NotNull BlobMobData data;
    private @Nullable EntityTracker entityTracker;

    public BlobMob(@NotNull BlobMobSpawner spawner,
                   @NotNull Mob mob,
                   double spawnAsLegendaryChance,
                   @NotNull BlobMobData data) {
        this.spawner = spawner;
        this.mob = mob;
        this.spawnAsLegendaryChance = spawnAsLegendaryChance;
        this.data = data;
    }

    public boolean isLegendary() {
        return spawnAsLegendaryChance <= data.chance();
    }

    public @NotNull BlobMobSpawner getSpawner() {
        return spawner;
    }

    public @NotNull Mob getMob() {
        return mob;
    }

    public double getSpawnAsLegendaryChance() {
        return spawnAsLegendaryChance;
    }

    public @NotNull BlobMobData getData() {
        return data;
    }

    public @Nullable EntityTracker getEntityTracker() {
        return entityTracker;
    }

    public void setEntityTracker(@Nullable EntityTracker entityTracker) {
        if (this.entityTracker != null) {
            this.entityTracker.close();
        }
        this.entityTracker = entityTracker;
    }
}
