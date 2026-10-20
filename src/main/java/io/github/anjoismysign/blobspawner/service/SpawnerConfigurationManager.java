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
    private SpawnerConfiguration configuration;

    public SpawnerConfigurationManager(SpawnerManagerDirector managerDirector) {
        super(managerDirector);
        reload();
    }

    @Override
    public void reload() {
        BlobSpawner plugin = getPlugin();
        plugin.saveResource("config.yml", false);

        File configFile = new File(plugin.getDataFolder(), "config.yml");
        Constructor configConstructor = new Constructor(SpawnerConfiguration.class, new LoaderOptions());
        Yaml configYaml = new Yaml(configConstructor);
        try (FileInputStream inputStream = new FileInputStream(configFile)) {
            configuration = configYaml.load(inputStream);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @NotNull
    public SpawnerConfiguration getConfiguration() {
        return configuration;
    }
}
