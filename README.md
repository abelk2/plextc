# Plex Transcode Cleaner

## What's this
Cleans up the original versions of movies and series from your Plex libraries.

This action is performed on launch, and also when changes are detected in the libraries (via filesystem watch).

Requires the [Optimized Versions](https://support.plex.tv/articles/213095317-creating-optimized-versions/) feature to be turned on for your libraries,
and the optimized versions should be configured to be stored at the same location (currently the only option in Plex).

## Why
Some Plex clients do not automatically pick the version of movies/episodes that is the most suitable for them.
Instead, you're prompted to choose one. Every single time.

This is a hacky solution that clears the original version when a version optimized by Plex appears. So only the optimized version will exist.

## Behavior
### For movies
The app will watch files in the base dir `${plextc.moviesRootDirectory}` matching the glob `**/Plex Versions/Optimized for TV/?*.{mkv,mp4,avi}` by default.
If anything matching appears, it will clear all video files in the directory of the original movie
(i.e. in the directory where `Plex Versions` resides).

### For series
The app will watch files in the base dir `${plextc.seriesRootDirectory}` matching the glob `**/Plex Versions/Optimized for TV/S??E??.{mkv,mp4,avi}` by default.
If anything matching appears, it will clear the file that contains the episode identifier (e.g. `S01E02`) in the directory of the original series
(i.e. in the directory where `Plex Versions` resides).

## Configuration
| Config key                   | Required | Default          | Description                                                                      |
|------------------------------|----------|------------------|----------------------------------------------------------------------------------|
| plextc.videoFileExtensions   | true     | mkv,mp4,avi      | The video formats to watch.                                                      |
| plextc.plexVersionName       | true     | Optimized for TV | The name of the optimized version. Path for the optimized versions depend on it. |
| plextc.loggingPath           | true     | ./logs           | The logging path of the app.                                                     |
| plextc.loggingLevel          | true     | INFO             | The logging level of the app.                                                    |
| plextc.moviesRootDirectory   | true     | -                | The root directory of the movies library.                                        |
| plextc.seriesRootDirectory   | true     | -                | The root directory of the series library.                                        |

Properties are read from the following locations (in the same priority order):
1. Environment variables (in format `PLEXTC_videoFileExtensions`)
2. System properties (i.e. `-Dplextc.videoFileExtensions`)
3. `plextc.properties` placed next to JAR
4. `plextc.properties` on classpath

## Warning, license

This program will **permanently delete** matched files as described above. Use at on your own risk! No warranty and support provided.

Licensed under the [MIT license](https://spdx.org/licenses/MIT.html)
