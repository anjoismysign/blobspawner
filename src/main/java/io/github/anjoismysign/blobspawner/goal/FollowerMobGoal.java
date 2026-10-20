package io.github.anjoismysign.blobspawner.goal;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import io.github.anjoismysign.blobspawner.BlobSpawner;
import io.github.anjoismysign.blobspawner.domain.BlobMob;
import io.github.anjoismysign.blobspawner.service.BlobMobManager;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;

public class FollowerMobGoal implements Goal<@NotNull Mob> {

    private static final GoalKey<@NotNull Mob> key = GoalKey.of(Mob.class, new NamespacedKey(BlobSpawner.getInstance(), "follower_mob_goal"));

    private Mob legendaryEntity;
    private final Mob entity;
    private final double awareDistance;

    public FollowerMobGoal(Mob entity, double awareDistance) {
        this.entity = entity;
        this.awareDistance = awareDistance;
    }

    @Override
    public boolean shouldActivate() {
        Location location = entity.getLocation();
        for (Entity entity : location.getNearbyEntities(awareDistance, awareDistance, awareDistance)) {
            if (entity.getType() != this.entity.getType()) {
                continue;
            }
            UUID uuid = entity.getUniqueId();
            if (uuid.equals(this.entity.getUniqueId())) {
                continue;
            }
            @Nullable BlobMob legendary = BlobMobManager.getSpawnedBlobMob(uuid);
            if (legendary == null || !legendary.isLegendary()) {
                continue;
            }
            if (!isWithinAwareDistance(entity)) {
                continue;
            }
            this.legendaryEntity = legendary.getMob();
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldStayActive() {
        return legendaryEntity.isValid() && isWithinAwareDistance(legendaryEntity);
    }

    private boolean isWithinAwareDistance(@NotNull Entity other) {
        if (!other.getWorld().equals(entity.getWorld())) {
            return false;
        }
        return entity.getLocation().distanceSquared(other.getLocation()) <= awareDistance * awareDistance;
    }

    @Override
    public void stop() {
        entity.getPathfinder().stopPathfinding();
        legendaryEntity = null;
    }

    @Override
    public void tick() {
        entity.getPathfinder().moveTo(legendaryEntity);
    }

    @Override
    public @NotNull
    GoalKey<@NotNull Mob> getKey() {
        return key;
    }

    @Override
    public @NotNull
    EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.MOVE);
    }
}
