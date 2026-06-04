package io.github.anjoismysign.blobspawner.domain;

import io.github.anjoismysign.anjo.entities.Uber;
import io.github.anjoismysign.blobspawner.BlobSpawner;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public record BlobMobSpawnerTask(@NotNull BukkitTask task,
                                 @NotNull BlobMobSpawner spawner) {

    private static final Random RANDOM = new Random();

    @NotNull
    private static Integer delay(@NotNull BlobMobSpawnerData data) {
        return RANDOM.nextInt(data.minDelay(), data.maxDelay() + 1);
    }

    @NotNull
    public static BlobMobSpawnerTask of(@NotNull BlobMobSpawner spawner) {
        var data = spawner.getData();
        Uber<Integer> delay = Uber.drive(delay(data));
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                int currentDelay = delay.thanks();
                if (currentDelay <= 0) {
                    if (spawner.fitsAnotherEntity()) {
                        spawner.spawnEntity();
                    }
                    delay.talk(delay(data));
                } else {
                    delay.talk(currentDelay - 1);
                }
            }
        }.runTaskTimer(BlobSpawner.getInstance(), 0, 1);
        return new BlobMobSpawnerTask(task, spawner);
    }
}
