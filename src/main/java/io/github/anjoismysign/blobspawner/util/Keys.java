package io.github.anjoismysign.blobspawner.util;

import io.github.anjoismysign.blobspawner.BlobSpawner;
import org.bukkit.NamespacedKey;

public enum Keys {
    BLOB_MOB_SPAWN_EGG("blob_mob_spawn_egg");

    private final NamespacedKey namespacedKey;

    Keys(String key) {
        this.namespacedKey = new NamespacedKey(BlobSpawner.getInstance(), key);
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }
}
