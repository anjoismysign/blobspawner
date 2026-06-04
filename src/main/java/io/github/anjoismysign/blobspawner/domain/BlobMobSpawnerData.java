package io.github.anjoismysign.blobspawner.domain;

import io.github.anjoismysign.bloblib.utilities.SerializationLib;
import io.github.anjoismysign.blobspawner.BlobSpawner;
import io.github.anjoismysign.holoworld.asset.DataAsset;
import io.github.anjoismysign.holoworld.asset.IdentityGeneration;
import io.github.anjoismysign.holoworld.asset.IdentityGenerator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record BlobMobSpawnerData(@NotNull String identifier,
                                 @NotNull List<String> blocksReferences,
                                 int minDelay,
                                 int maxDelay,
                                 int maxCount)
        implements DataAsset {

    @NotNull
    public BlobMobData getBlobMobData() {
        @Nullable var generation = BlobSpawner.getInstance().getBlobMobIdentityManager().fetchGeneration(identifier);
        Objects.requireNonNull(generation, "No BlobMob goes by \"" + identifier + "\"");
        return generation.asset();
    }

    @NotNull
    public List<Block> fetchBlocks() {
        return blocksReferences.stream()
                .map(SerializationLib::deserializeLocation)
                .map(Location::getBlock)
                .toList();
    }

    @NotNull
    public IdentityGeneration<BlobMobSpawnerData> generation() {
        Info info = new Info();
        info.setMinDelay(minDelay);
        info.setMaxDelay(maxDelay);
        info.setMaxCount(maxCount);
        info.setBlocks(blocksReferences);
        return new IdentityGeneration<>(identifier, info);
    }

    public static final class Info
            implements IdentityGenerator<BlobMobSpawnerData> {
        private int minDelay;
        private int maxDelay;
        private int maxCount;
        private @NotNull List<String> blocks;

        @Override
        public @NotNull BlobMobSpawnerData generate(@NotNull String identifier) {
            @Nullable var generation = BlobSpawner.getInstance().getBlobMobIdentityManager().fetchGeneration(identifier);
            Objects.requireNonNull(generation, "No BlobMob goes by \"" + identifier + "\"");
            return new BlobMobSpawnerData(
                    identifier,
                    new ArrayList<>(blocks),
                    minDelay,
                    maxDelay,
                    maxCount);
        }

        public int getMinDelay() {
            return minDelay;
        }

        public void setMinDelay(int minDelay) {
            this.minDelay = minDelay;
        }

        public int getMaxDelay() {
            return maxDelay;
        }

        public void setMaxDelay(int maxDelay) {
            this.maxDelay = maxDelay;
        }

        public int getMaxCount() {
            return maxCount;
        }

        public void setMaxCount(int maxCount) {
            this.maxCount = maxCount;
        }

        public @NotNull List<String> getBlocks() {
            return blocks;
        }

        public void setBlocks(@NotNull List<String> blocks) {
            this.blocks = blocks;
        }
    }
}
