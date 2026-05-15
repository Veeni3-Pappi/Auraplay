# 🎵 Auraplay — Android Music Player

**Package:** `com.aceshot.musicplayer`
**Min SDK:** API 30 (Android 11)
**Design Reference:** [PixelPlayer](https://github.com/theovilardo/PixelPlayer) (design sensibility only — built from scratch)

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 100% |
| UI | Jetpack Compose |
| Design System | Material Design 3 (Material You) |
| Audio Engine | Media3 ExoPlayer |
| Architecture | MVVM + Clean Architecture (StateFlow/SharedFlow) |
| DI | Hilt |
| Database | Room (with FTS for search) |
| Image Loading | Coil |
| Async | Kotlin Coroutines + Flow |
| Preferences | Jetpack DataStore |

---

## Project Structure

```
app/src/main/java/com/aceshot/musicplayer/
├── data/
│   ├── database/        # Room entities, DAOs, FTS tables
│   ├── model/           # Domain models: Song, Album, Artist, Genre, Folder
│   ├── preferences/     # DataStore: onboarding, settings, shuffle state
│   ├── repository/      # MusicRepository, PlaylistRepository, FolderFilterRepository
│   └── scanner/         # MediaStore scanner + exclusion logic
├── di/                  # Hilt modules
├── player/
│   ├── MusicService.kt  # MediaSessionService + ExoPlayer setup
│   ├── SmartShuffleQueue.kt  # Custom ShuffleOrder (Fisher-Yates + Recency + Artist Spread)
│   └── QueueManager.kt  # Queue state, recency buffer
├── presentation/
│   ├── components/      # Reusable Compose components
│   ├── navigation/      # NavGraph, routes, bottom bar
│   ├── screens/
│   │   ├── library/     # Songs, Albums, Artists, Genres, Folders tabs
│   │   ├── nowplaying/  # Full player with blurred album art background
│   │   ├── playlists/   # Create, delete, reorder playlists
│   │   ├── search/      # Full-text search with FTS
│   │   ├── settings/    # Appearance, Playback, Library, About
│   │   └── onboarding/  # 3-screen intro flow
│   └── viewmodel/
└── ui/
    └── theme/           # Colors, typography, Material You theme
```

---

## Current Progress

### ✅ Phase 1: Project Scaffolding (Partial)

Everything related to Gradle build system and project config is **done**.

| File | Status | Description |
|------|--------|-------------|
| `settings.gradle.kts` | ✅ Done | Plugin management, dependency resolution, root project name |
| `build.gradle.kts` (root) | ✅ Done | Plugin declarations (AGP, Kotlin, Hilt, KSP, Compose) |
| `app/build.gradle.kts` | ✅ Done | Full app module config with all dependencies |
| `gradle/libs.versions.toml` | ✅ Done | Version catalog (Compose, Hilt, Room, Media3, Coil, DataStore, etc.) |
| `gradle.properties` | ✅ Done | JVM args, AndroidX config |
| `gradle-wrapper.properties` | ✅ Done | Gradle 8.11.1 |
| `.gitignore` | ✅ Done | Standard Android gitignore |
| `proguard-rules.pro` | ✅ Done | Placeholder |
| `AndroidManifest.xml` | ✅ Done | Permissions, service declarations, activities |
| `AuraplayApp.kt` | ✅ Done | Hilt Application class |
| **Resources** | ✅ Done | `strings.xml`, `colors.xml`, `themes.xml`, adaptive launcher icons |

### ✅ Phase 2: Data Layer (Partial)

**Domain models and Room database layer are done.** Scanner, repositories, and DataStore are still pending.

#### Domain Models — All Done ✅
| File | Description |
|------|-------------|
| `data/model/Song.kt` | Core song model with URI, metadata, duration |
| `data/model/Album.kt` | Album with song count, art URI |
| `data/model/Artist.kt` | Artist with album/song counts |
| `data/model/Genre.kt` | Genre grouping |
| `data/model/Folder.kt` | Folder-based browsing model |

#### Room Entities — All Done ✅
| File | Description |
|------|-------------|
| `data/database/SongEntity.kt` | Song table with all metadata columns |
| `data/database/SongFts.kt` | FTS4 virtual table for full-text search |
| `data/database/PlaylistEntity.kt` | Playlist table |
| `data/database/PlaylistSongCrossRef.kt` | Many-to-many playlist ↔ song join table |
| `data/database/ExcludedFolderEntity.kt` | Excluded folder paths for smart filtering |
| `data/database/QueueEntity.kt` | Persisted playback queue |
| `data/database/RecentSearchEntity.kt` | Search history |

#### Room DAOs — All Done ✅
| File | Description |
|------|-------------|
| `data/database/SongDao.kt` | CRUD + sorted queries + FTS search |
| `data/database/PlaylistDao.kt` | Playlist management + song associations |
| `data/database/ExcludedFolderDao.kt` | Add/remove/list excluded folders |
| `data/database/QueueDao.kt` | Save/restore playback queue |
| `data/database/RecentSearchDao.kt` | Search history management |

#### Room Database — Done ✅
| File | Description |
|------|-------------|
| `data/database/AuraplayDatabase.kt` | Database class with all entities and DAOs registered |

---

## 🔲 What's Left To Build

### Phase 1 Remaining: Theme & Navigation

| File | Description | Priority |
|------|-------------|----------|
| `ui/theme/Color.kt` | Material You color scheme (light/dark, dynamic) | 🔴 High |
| `ui/theme/Type.kt` | Typography scale using Google Fonts | 🔴 High |
| `ui/theme/Theme.kt` | Compose MaterialTheme wrapper with dynamic color | 🔴 High |
| `di/AppModule.kt` | Hilt module for database, DataStore, repositories | 🔴 High |
| `di/PlayerModule.kt` | Hilt module for ExoPlayer, MusicService | 🔴 High |
| `presentation/navigation/BottomNavBar.kt` | Bottom navigation (Library, Search, Playlists, Settings) | 🔴 High |
| `presentation/navigation/NavGraph.kt` | Compose Navigation graph with all routes | 🔴 High |
| `MainActivity.kt` | Root activity with Hilt, theme, and nav host | 🔴 High |

---

### Phase 2 Remaining: DataStore, Scanner, Repositories

| File | Description | Priority |
|------|-------------|----------|
| `data/preferences/UserPreferences.kt` | DataStore for onboarding state, theme, playback settings | 🔴 High |
| `data/scanner/MediaStoreScanner.kt` | Query MediaStore for audio files, apply 60s filter | 🔴 High |
| `data/scanner/FolderExclusionManager.kt` | Smart exclusion (WhatsApp, voice memos, call recordings) | 🔴 High |
| `data/repository/MusicRepository.kt` | Bridges scanner → Room, provides Flow-based data | 🔴 High |
| `data/repository/PlaylistRepository.kt` | Playlist CRUD operations | 🟡 Medium |
| `data/repository/SettingsRepository.kt` | Wraps DataStore preferences | 🟡 Medium |

---

### Phase 3: Player Layer

| File | Description | Priority |
|------|-------------|----------|
| `player/MusicService.kt` | MediaSessionService with ExoPlayer, notifications, lock screen controls | 🔴 High |
| `player/SmartShuffleQueue.kt` | Custom ShuffleOrder: Fisher-Yates + recency buffer + artist spread | 🔴 High |
| `player/QueueManager.kt` | Queue state management, crossfade config | 🔴 High |

---

### Phase 4: Presentation Layer (UI)

| File | Description | Priority |
|------|-------------|----------|
| **Onboarding** | | |
| `screens/onboarding/OnboardingScreen.kt` | 3-screen intro: Welcome → Permissions → Smart Filter | 🟡 Medium |
| `viewmodel/OnboardingViewModel.kt` | Manages onboarding state via DataStore | 🟡 Medium |
| **Library** | | |
| `screens/library/LibraryScreen.kt` | Tabbed view: Songs, Albums, Artists, Genres, Folders | 🔴 High |
| `screens/library/SongListItem.kt` | Individual song row component | 🔴 High |
| `screens/library/AlbumGrid.kt` | Album grid with cover art | 🔴 High |
| `viewmodel/LibraryViewModel.kt` | Song/album/artist data + sorting | 🔴 High |
| **Now Playing** | | |
| `screens/nowplaying/NowPlayingScreen.kt` | Full player: art, controls, seek bar, blurred background | 🔴 High |
| `viewmodel/NowPlayingViewModel.kt` | Playback state, progress, controls | 🔴 High |
| **Mini-Player** | | |
| `components/MiniPlayer.kt` | Persistent bar above bottom nav | 🔴 High |
| **Search** | | |
| `screens/search/SearchScreen.kt` | Search bar + FTS results + recent searches | 🟡 Medium |
| `viewmodel/SearchViewModel.kt` | FTS query execution, search history | 🟡 Medium |
| **Playlists** | | |
| `screens/playlists/PlaylistsScreen.kt` | List/create/delete playlists | 🟡 Medium |
| `screens/playlists/PlaylistDetailScreen.kt` | Songs in a playlist, reorder, remove | 🟡 Medium |
| `viewmodel/PlaylistViewModel.kt` | Playlist CRUD | 🟡 Medium |
| **Settings** | | |
| `screens/settings/SettingsScreen.kt` | Appearance, Playback, Library rescan, About | 🟢 Low |
| `viewmodel/SettingsViewModel.kt` | Settings state | 🟢 Low |
| **Shared Components** | | |
| `components/SortMenu.kt` | Dropdown for sort options | 🟡 Medium |
| `components/ArtworkImage.kt` | Coil-based album art with placeholder | 🟡 Medium |
| `components/AnimatedPlayButton.kt` | Play/pause with morph animation | 🟡 Medium |

---

### Phase 5: Polish & Resources

| Item | Description | Priority |
|------|-------------|----------|
| Gradle wrapper JAR | `gradle/wrapper/gradle-wrapper.jar` (needed to build) | 🔴 High |
| `gradlew` / `gradlew.bat` | Gradle wrapper scripts | 🔴 High |
| Drawable assets | Notification icons, placeholder art | 🟡 Medium |
| `README.md` | This file (update when done) | 🟢 Low |

---

### Phase 6: Verification

| Check | Description |
|-------|-------------|
| `./gradlew assembleDebug` | Project compiles without errors |
| Install on device/emulator | Onboarding flow works |
| Media scan test | Songs discovered, exclusions applied |
| Playback test | Play, pause, skip, shuffle, queue |
| Search test | FTS returns correct results |

---

## Build Progress Overview

```
Phase 1: Scaffolding & Theme    ██████████░░░░░░░░░░  50%
Phase 2: Data Layer             ████████████░░░░░░░░  60%
Phase 3: Player Layer           ░░░░░░░░░░░░░░░░░░░░   0%
Phase 4: Presentation (UI)      ░░░░░░░░░░░░░░░░░░░░   0%
Phase 5: Polish & Resources     ██░░░░░░░░░░░░░░░░░░  10%
Phase 6: Verification           ░░░░░░░░░░░░░░░░░░░░   0%
─────────────────────────────────────────────────────────
Overall                         ████░░░░░░░░░░░░░░░░  ~20%
```

---

## Key Design Decisions

1. **Material You Dynamic Color** — Theme adapts to wallpaper on Android 12+, falls back to curated palette on Android 11
2. **FTS4 Search** — Full-text search across song titles, artists, and albums with instant results
3. **Smart Shuffle** — Fisher-Yates base with recency buffer (won't repeat recent 40%) and artist spread (no back-to-back same artist)
4. **60-Second Filter** — Automatically excludes audio files shorter than 60 seconds (ringtones, notifications)
5. **Smart Folder Exclusion** — Auto-excludes WhatsApp audio, call recordings, voice memos, alarms, and similar non-music folders

---

*Last updated: May 15, 2026*
