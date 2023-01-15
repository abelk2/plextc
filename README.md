# Plex Transcode Cleaner

## What's this
Replaces the original versions of movies and series episodes in your Plex libraries with their transcoded versions made by Plex.

This action is performed on launch, and also when changes are detected in the libraries (via filesystem watch).

Requires the [Optimized Versions](https://support.plex.tv/articles/213095317-creating-optimized-versions/) feature to be turned on for your libraries,
and the optimized versions should be configured to be stored at the same location (currently the only option in Plex).

## Why
Some Plex clients do not automatically pick the version of movies/episodes that is the most suitable for them.
Instead, you're prompted to choose one. Every single time.

This is a hacky solution that replaces the original version with its optimized version when it appears at the expected location.
So only the optimized version will exist.

## Behavior
### For movies
The app will watch files in the base dir `${plextc.moviesRootDirectory}` matching the glob `**/Plex Versions/${plextc.plexVersionName}/?*.mp4` by default.
If anything matching appears, it will replace the first video in the directory of the original movie (i.e. in the directory where `Plex Versions` resides). If nothing matches, it will just move the optimized movie to the mentioned directory without replacing anything.

### For series
The app will watch files in the base dir `${plextc.seriesRootDirectory}` matching the glob `**/Plex Versions/${plextc.plexVersionName}/**/[sS]??[eE]??.mp4` by default.
If anything matching appears, it will replace the file that contains the episode identifier (e.g. `S01E02`) in the directory of the original series
(i.e. in the directory where `Plex Versions` resides) with the optimized version. If nothing matches, it will just move the optimized movie to the
mentioned directory without replacing anything.

When there's an extension change (i.e. Plex remuxed the video file), it will move the converted video to the original directory, renaming it to the original file's name, but keeping the extension of the converted one. This has to be done with a few minutes delay, as Plex seemingly removes both videos if we replace it immediately.

## Configuration
| Config key                   | Required | Default          | Description                                                                      |
|------------------------------|----------|------------------|----------------------------------------------------------------------------------|
| plextc.videoFileExtensions   | true     | mkv,mp4,avi      | The video formats to replace with their optimized version.                       |
| plextc.plexVersionName       | true     | Optimized for TV | The name of the optimized version. Path for the optimized versions depend on it. |
| plextc.loggingPath           | true     | ./logs           | The logging path of the app.                                                     |
| plextc.loggingLevel          | true     | INFO             | The logging level of the app.                                                    |
| plextc.moviesRootDirectory   | true     | -                | The root directory of the movies library.                                        |
| plextc.seriesRootDirectory   | true     | -                | The root directory of the series library.                                        |
| plextc.extensionChangeReplaceDelayMinutes | true | - | The minutes to wait before replacing an original video file with its converted version when there's an extension change.|

Properties are read from the following locations (in the same priority order):
1. Environment variables (in format `PLEXTC_videoFileExtensions`)
2. System properties (i.e. `-Dplextc.videoFileExtensions`)
3. `plextc.properties` placed next to JAR
4. `plextc.properties` on classpath

## Warning, license

This program will **permanently delete** original versions of movies/episodes by replacing them as described above. Use at your own risk! No warranty and support provided.

Licensed under the [MIT license](https://spdx.org/licenses/MIT.html)
