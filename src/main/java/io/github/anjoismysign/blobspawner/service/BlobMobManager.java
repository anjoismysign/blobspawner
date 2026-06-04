package io.github.anjoismysign.blobspawner.service;

import io.github.anjoismysign.bloblib.api.BlobLibLootAPI;
import io.github.anjoismysign.bloblib.utilities.SerializationLib;
import io.github.anjoismysign.blobspawner.BlobSpawner;
import io.github.anjoismysign.blobspawner.domain.BlobMob;
import io.github.anjoismysign.blobspawner.domain.BlobMobData;
import io.github.anjoismysign.blobspawner.domain.BlobMobSpawner;
import io.github.anjoismysign.blobspawner.domain.BlobMobSpawnerData;
import io.github.anjoismysign.blobspawner.event.BlobMobDeathEvent;
import io.github.anjoismysign.blobspawner.util.Keys;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class BlobMobManager extends SpawnerManager implements Listener {
    private static BlobMobManager instance;
    private final Map<Block, BlobMobSpawner> spawners = new HashMap<>();

    public BlobMobManager(SpawnerManagerDirector managerDirector) {
        super(managerDirector);
        instance = this;
        reload();
        Bukkit.getPluginManager().registerEvents(this, managerDirector.getPlugin());
    }

    @Override
    public void reload() {
        spawners.forEach(((block, spawner) -> {
            spawner.killAll();
            spawner.getTask().task().cancel();
        }));
        Bukkit.getScheduler().runTask(getPlugin(), () -> {
            BlobSpawner blobSpawner = BlobSpawner.getInstance();
            blobSpawner.getBlobMobSpawnerIdentityManager().forEach(spawnerData -> {
                List<Block> blocks = spawnerData.fetchBlocks();
                blocks.forEach(block -> {
                    spawners.put(block, new BlobMobSpawner(spawnerData, block));
                });
            });
        });
    }

    @EventHandler
    public void remove(EntityRemoveEvent event) {
        Entity entity = event.getEntity();
        UUID uniqueId = entity.getUniqueId();
        @Nullable BlobMobSpawner belonging = spawners.values()
                .stream()
                .filter(spawner -> spawner.isTrackingEntity(uniqueId))
                .findFirst()
                .orElse(null);
        if (belonging == null) {
            return;
        }
        belonging.removeEntity(uniqueId, false);
    }

    @EventHandler
    public void spawnerSet(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        @Nullable Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (block.getType() != Material.SPAWNER) {
            return;
        }
        @Nullable ItemStack hand = event.getItem();
        if (hand == null) {
            return;
        }
        @Nullable ItemMeta itemMeta = hand.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        var persistentDataContainer = itemMeta.getPersistentDataContainer();
        @Nullable String spawnerIdentifier = persistentDataContainer.get(Keys.BLOB_MOB_SPAWN_EGG.getNamespacedKey(), PersistentDataType.STRING);
        if (spawnerIdentifier == null) {
            return;
        }
        @Nullable var generation = getPlugin().getBlobMobSpawnerIdentityManager().fetchGeneration(spawnerIdentifier);
        if (generation == null) {
            return;
        }
        BlobMobSpawnerData spawnerData = generation.asset();
        event.setCancelled(true);
        spawnerData.blocksReferences().add(SerializationLib.serialize(block.getLocation()));
        getPlugin().getBlobMobSpawnerIdentityManager().add(spawnerData.generation());
        block.setType(Material.AIR);
    }

    @EventHandler
    public void cancelSpawn(CreatureSpawnEvent event) {
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
        if (reason == CreatureSpawnEvent.SpawnReason.CUSTOM || reason == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void death(EntityDeathEvent event) {
        LivingEntity normal = event.getEntity();
        UUID uniqueId = normal.getUniqueId();
        @Nullable BlobMob blobMob = getBlobMobByUUID(uniqueId);
        if (blobMob == null) {
            return;
        }
        @Nullable BlobMobData blobMobData = blobMob.getData();
        @Nullable Mob mob = blobMob.getMob();
        List<ItemStack> drops = event.getDrops();
        drops.clear();
        boolean isLegendary = blobMob.isLegendary();
        String lootTable = !isLegendary ? blobMobData.defaultEntity().lootTable() : blobMobData.legendaryEntity().lootTable();
        if (!lootTable.isEmpty()) {
            drops.addAll(BlobLibLootAPI.getInstance().generateLoot(lootTable, null));
        }
        if (!isLegendary) {
            return;
        }
        BlobMobDeathEvent blobMobDeathEvent = new BlobMobDeathEvent(blobMob, drops);
        Bukkit.getPluginManager().callEvent(blobMobDeathEvent);
    }

    @Nullable
    public BlobMob getBlobMobByUUID(@NotNull UUID uuid) {
        return spawners.values()
                .stream()
                .map(spawner -> spawner.getBlobMob(uuid))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Nullable
    public static Mob getSpawned(@NotNull UUID uuid) {
        if (instance == null) {
            return null;
        }
        @Nullable var blobMob = instance.getBlobMobByUUID(uuid);
        if (blobMob == null) {
            return null;
        }
        return blobMob.getMob();
    }
}
