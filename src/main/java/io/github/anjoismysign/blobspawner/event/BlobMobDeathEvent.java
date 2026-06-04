package io.github.anjoismysign.blobspawner.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BlobMobDeathEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final LivingEntity entity;
    private final List<ItemStack> drops;

    public BlobMobDeathEvent(LivingEntity entity,
                             List<ItemStack> drops) {
        this.entity = entity;
        this.drops = drops;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public List<ItemStack> getDrops() {
        return drops;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
