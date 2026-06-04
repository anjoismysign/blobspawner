package io.github.anjoismysign.blobspawner.service;

import io.github.anjoismysign.blobspawner.BlobSpawner;
import io.github.anjoismysign.blobspawner.configuration.SpawnerConfiguration;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SpawnerConfigurationManager extends SpawnerManager {
    private static SpawnerConfiguration configuration;

    public SpawnerConfigurationManager(SpawnerManagerDirector managerDirector) {
        super(managerDirector);
        reload();
    }

    @Override
    public void reload() {
        BlobSpawner plugin = getPlugin();
        plugin.saveResource("config.yml", false);
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        Constructor constructor = new Constructor(SpawnerConfiguration.class, new LoaderOptions());
        Yaml yaml = new Yaml(constructor);
        try (FileInputStream inputStream = new FileInputStream(configFile)) {
            configuration = yaml.load(inputStream);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @NotNull
    public static SpawnerConfiguration getConfiguration() {
        return configuration;
    }
}
