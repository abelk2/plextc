package eu.abelk.plextc.util;

import ch.kk7.confij.binding.values.SeparatedMapper.Separated;
import ch.kk7.confij.validation.NonNullValidator.NotNull;

import java.util.Set;

public interface Config {

    @NotNull
    @Separated
    Set<String> videoFileExtensions();

    @NotNull
    String moviesRootDirectory();

    @NotNull
    String seriesRootDirectory();

    @NotNull
    String plexVersionName();

    @NotNull
    String loggingPath();

    @NotNull
    String loggingLevel();

}
