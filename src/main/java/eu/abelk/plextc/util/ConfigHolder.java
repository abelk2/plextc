package eu.abelk.plextc.util;

import ch.kk7.confij.ConfijBuilder;
import ch.kk7.confij.source.env.EnvvarSource;
import ch.kk7.confij.source.env.SystemPropertiesSource;
import ch.kk7.confij.source.format.PropertiesFormat;
import ch.kk7.confij.source.resource.ClasspathResource;
import ch.kk7.confij.source.resource.FileResource;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public enum ConfigHolder {

    INSTANCE;

    private static final String PROPERTIES_FILE_NAME = "plextc.properties";
    private static final String PROPERTY_PREFIX = "plextc";

    private final Config config;

    ConfigHolder() {
        ConfijBuilder<Config> builder = ConfijBuilder.of(Config.class)
            .loadFrom(ClasspathResource.ofName(PROPERTIES_FILE_NAME), new PropertiesFormat(".", PROPERTY_PREFIX));

        String nextToJarConfig = getPropertiesFilePathNextToJar();
        if (nextToJarConfig != null) {
            builder = builder.loadFrom(FileResource.ofFile(nextToJarConfig), new PropertiesFormat(".", PROPERTY_PREFIX));
        }

        this.config = builder.loadFrom(SystemPropertiesSource.withPrefix(PROPERTY_PREFIX))
            .loadFrom(EnvvarSource.withPrefix(PROPERTY_PREFIX.toUpperCase(Locale.ROOT)))
            .build();
    }

    public static Config getConfig() {
        return INSTANCE.config;
    }

    @SneakyThrows
    private static String getPropertiesFilePathNextToJar() {
        String result = null;
        Path path = Paths.get(ConfigHolder.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        if (path.toString().endsWith(".jar")) {
            File propertiesFilePath = path.resolve("..")
                .resolve(PROPERTIES_FILE_NAME)
                .normalize()
                .toFile();
            if (propertiesFilePath.exists()) {
                result = propertiesFilePath.getPath().replaceAll("\\\\", "/");
            }
        }
        return result;
    }

}
