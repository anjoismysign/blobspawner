package io.github.anjoismysign.blobspawner.domain;

import io.github.anjoismysign.blobspawner.event.BlobMobSpawnEvent;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class BlobMobSpawner {
    private final @NotNull BlobMobSpawnerData data;
    private final @NotNull Block block;
    private @NotNull BlobMobSpawnerTask task;
    private final @NotNull Set<BlobMob> entities = new HashSet<>();

    public BlobMobSpawner(@NotNull BlobMobSpawnerData data,
                          @NotNull Block block) {
        this.data = data;
        this.block = block;
        restartTask();
    }

    public void killAll() {
        List<UUID> uuids = new ArrayList<>();
        entities.forEach(blobMob -> {
            uuids.add(blobMob.getMob().getUniqueId());
        });
        uuids.forEach(uuid->removeEntity(uuid, true));
    }

    public boolean fitsAnotherEntity() {
        var currentCount = entities.size();
        var maxCount = data.maxCount();
        return currentCount < maxCount;
    }

    public void spawnEntity() {
        var blobMobData = data.getBlobMobData();
        Location location = block.getLocation().toCenterLocation();
        Mob mob = (Mob) location.getWorld().spawnEntity(location, blobMobData.type());
        double chance = Math.random();
        BlobMob blobMob = new BlobMob(this, mob, chance, blobMobData);
        boolean isLegendary = blobMob.isLegendary();
        String model = isLegendary ? blobMobData.legendaryEntity().model() : blobMobData.defaultEntity().model();
        if (!model.isEmpty()) {
            mob.setSilent(true);
            BetterModel.model(model)
                    .map(renderer -> renderer.getOrCreate(BukkitAdapter.adapt(mob)))
                    .ifPresent(blobMob::setEntityTracker);
        }
        mob.setPersistent(false);
        blobMobData.instantiate(mob, isLegendary);
        entities.add(blobMob);
        BlobMobSpawnEvent blobMobSpawnEvent = new BlobMobSpawnEvent(blobMob, this);
        Bukkit.getPluginManager().callEvent(blobMobSpawnEvent);
    }

    @Nullable
    public BlobMob getBlobMob(@NotNull UUID uuid) {
        return entities.stream().filter(animal -> animal.getMob().getUniqueId().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public boolean isTrackingEntity(@NotNull UUID uuid) {
        @Nullable var blobMob = getBlobMob(uuid);
        return blobMob != null;
    }

    public void removeEntity(@NotNull UUID uuid, boolean removeUnderlyingMob) {
        @Nullable var blobMob = entities.stream().filter(animal -> animal.getMob().getUniqueId().equals(uuid))
                .findFirst()
                .orElse(null);
        if (blobMob == null) {
            return;
        }
        blobMob.setEntityTracker(null);
        if (removeUnderlyingMob) {
            blobMob.getMob().remove();
        }
    }

    public void restartTask() {
        task = BlobMobSpawnerTask.of(this);
    }

    public @NotNull BlobMobSpawnerData getData() {
        return data;
    }

    public @NotNull Block getBlock() {
        return block;
    }

    public @NotNull BlobMobSpawnerTask getTask() {
        return task;
    }
}
