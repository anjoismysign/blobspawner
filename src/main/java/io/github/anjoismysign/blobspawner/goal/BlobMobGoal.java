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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Random;
import java.util.UUID;

public class BlobMobGoal implements Goal<@NotNull Mob> {
    private static final GoalKey<@NotNull Mob> key = GoalKey.of(Mob.class, new NamespacedKey(BlobSpawner.getInstance(), "blob_mob_goal"));
    private static final Random random = new Random();
    private final Mob entity;
    private final double awareDistance;
    private final double attackDistance;
    private LivingEntity enemy;
    private int cooldown;

    public BlobMobGoal(Mob entity, double awareDistance, double attackDistance) {
        this.entity = entity;
        this.awareDistance = awareDistance;
        this.attackDistance = attackDistance;
    }

    @Override
    public boolean shouldActivate() {
        Location location = entity.getLocation();
        @Nullable Player targetPlayer = null;
        for (Entity entity : location.getNearbyEntities(awareDistance, awareDistance, awareDistance)) {
            if (!isWithinAwareDistance(entity)) {
                continue;
            }
            if (entity instanceof Player player) {
                if (targetPlayer == null && isTargetablePlayer(player)) {
                    targetPlayer = player;
                }
                continue;
            }
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
            // rival legendaries take priority over players
            enemy = legendary.getMob();
            return true;
        }
        if (targetPlayer == null) {
            return false;
        }
        enemy = targetPlayer;
        return true;
    }

    private boolean isTargetablePlayer(@NotNull Player player) {
        if (player.hasMetadata("NPC")) {
            return false;
        }
        if (player.getGameMode().isInvulnerable()) {
            return false;
        }
        return !player.isInvulnerable();
    }

    @Override
    public boolean shouldStayActive() {
        return enemy.isValid() && isWithinAwareDistance(enemy);
    }

    private boolean isWithinAwareDistance(@NotNull Entity other) {
        if (!other.getWorld().equals(entity.getWorld())) {
            return false;
        }
        return entity.getLocation().distanceSquared(other.getLocation()) <= awareDistance * awareDistance;
    }

    @Override
    public void stop() {
        this.enemy = null;
    }

    @Override
    public void tick() {
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        if (entity.getLocation().distanceSquared(enemy.getLocation()) >= attackDistance * attackDistance) {
            entity.getPathfinder().moveTo(enemy, 2.0);
            return;
        }
        entity.teleport(enemy);
        entity.attack(enemy);
        this.cooldown = generateCooldown();
    }

    @Override
    public @NotNull
    GoalKey<@NotNull Mob> getKey() {
        return key;
    }

    @Override
    public @NotNull
    EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.TARGET, GoalType.MOVE);
    }

    private int generateCooldown() {
        return random.nextInt(20);
    }
}
