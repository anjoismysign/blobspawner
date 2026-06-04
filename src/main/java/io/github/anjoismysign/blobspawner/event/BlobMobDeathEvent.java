package io.github.anjoismysign.blobspawner.event;

import io.github.anjoismysign.blobspawner.domain.BlobMob;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BlobMobDeathEvent extends BlobMobEvent {
    private static final HandlerList handlers = new HandlerList();
    private final List<ItemStack> drops;

    public BlobMobDeathEvent(BlobMob blobMob,
                             List<ItemStack> drops) {
        super(false, blobMob);
        this.drops = drops;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public LivingEntity getEntity() {
        return getBlobMob().getMob();
    }

    public List<ItemStack> getDrops() {
        return drops;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
