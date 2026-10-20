package io.github.anjoismysign.blobspawner.listener;

import io.github.anjoismysign.bloblib.listener.SmartTinyListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CancelNaturalSpawns implements SmartTinyListener {

    @EventHandler
    public void cancelSpawn(CreatureSpawnEvent event) {
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
        if (reason == CreatureSpawnEvent.SpawnReason.CUSTOM || reason == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            return;
        }
        event.setCancelled(true);
    }

}
