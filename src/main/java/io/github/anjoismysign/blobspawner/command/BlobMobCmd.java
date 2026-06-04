package io.github.anjoismysign.blobspawner.command;

import io.github.anjoismysign.bloblib.api.BlobLibMessageAPI;
import io.github.anjoismysign.bloblib.utilities.PlayerUtil;
import io.github.anjoismysign.blobspawner.BlobSpawner;
import io.github.anjoismysign.blobspawner.util.Keys;
import io.github.anjoismysign.skeramidcommands.command.Command;
import io.github.anjoismysign.skeramidcommands.commandtarget.BukkitCommandTarget;
import io.github.anjoismysign.skeramidcommands.server.bukkit.BukkitAdapter;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public enum BlobMobCmd {
    INSTANCE;

    public void initialize() {
        var players = BukkitCommandTarget.ONLINE_PLAYERS();
        var blobMobIdentityManager = BlobSpawner.getInstance().getBlobMobIdentityManager();

        Command main = BukkitAdapter.getInstance().ofBukkitCommand("blobmob");
        Command egg = main.child("egg");
        Command give = egg.child("give");
        give.setParameters(players, blobMobIdentityManager);
        give.onExecute((permissionMessenger, args) -> {
            CommandSender commandSender = BukkitAdapter.getInstance().of(permissionMessenger);
            if (args.length < 2) {
                return;
            }
            Player target = players.parse(args[0]);
            if (target == null) {
                BlobLibMessageAPI.getInstance()
                        .getMessage("Player.Not-Found", commandSender)
                        .toCommandSender(commandSender);
                return;
            }
            @Nullable var blobMobData = blobMobIdentityManager.parse(args[1]);
            if (blobMobData == null) {
                return;
            }
            @Nullable Material material = Material.matchMaterial(blobMobData.type().name() + "_SPAWN_EGG");
            if (material == null) {
                return;
            }
            ItemStack itemStack = new ItemStack(material);
            @Nullable ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) {
                return;
            }
            var persistentDataContainer = itemMeta.getPersistentDataContainer();
            persistentDataContainer.set(Keys.BLOB_MOB_SPAWN_EGG.getNamespacedKey(), PersistentDataType.STRING, blobMobData.identifier());
            itemStack.setItemMeta(itemMeta);
            PlayerUtil.giveItemToInventoryOrDrop(target, itemStack);
        });
    }
}
