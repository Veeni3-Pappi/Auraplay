package com.aceshot.musicplayer.presentation.screens.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aceshot.musicplayer.data.model.Album
import com.aceshot.musicplayer.data.model.Artist
import com.aceshot.musicplayer.data.model.Folder
import com.aceshot.musicplayer.data.model.Genre
import com.aceshot.musicplayer.data.model.Song
import com.aceshot.musicplayer.presentation.components.SortMenu
import com.aceshot.musicplayer.presentation.viewmodel.LibraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onPlaySongs: (List<Song>, Int) -> Unit = { _, _ -> },
    onAddToPlaylist: (Song) -> Unit = {},
    viewModel: LibraryViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Songs", "Albums", "Artists", "Genres", "Folders")
    
    val songs by viewModel.songs.collectAsStateWithLifecycle()
    val albums by viewModel.albums.collectAsStateWithLifecycle()
    val artists by viewModel.artists.collectAsStateWithLifecycle()
    val genres by viewModel.genres.collectAsStateWithLifecycle()
    val folders by viewModel.folders.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()
    val filteredSongs by viewModel.filteredSongs.collectAsStateWithLifecycle()
    val filterTitle by viewModel.filterTitle.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        if (filterTitle != null) {
            TopAppBar(
                title = { Text(filterTitle ?: "") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.clearFilter() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredSongs, key = { it.id }) { song ->
                    SongListItem(
                        song = song,
                        onClick = { onPlaySongs(filteredSongs, filteredSongs.indexOf(song)) },
                        onMenuClick = { onAddToPlaylist(song) }
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 16.dp,
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.surface,
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
                if (selectedTab == 0) {
                    SortMenu(currentSort = sortOrder, onSortSelected = { viewModel.setSortOrder(it) })
                }
            }

            when (selectedTab) {
                0 -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(songs, key = { it.id }) { song ->
                            SongListItem(
                                song = song,
                                onClick = { onPlaySongs(songs, songs.indexOf(song)) },
                                onMenuClick = { onAddToPlaylist(song) }
                            )
                        }
                    }
                }
                1 -> {
                    AlbumGrid(
                        albums = albums,
                        onAlbumClick = { album -> viewModel.filterByAlbum(album.id, album.name) }
                    )
                }
                2 -> {
                    ArtistList(artists = artists, onArtistClick = { artist -> viewModel.filterByArtist(artist.name) })
                }
                3 -> {
                    GenreList(genres = genres, onGenreClick = { genre -> viewModel.filterByGenre(genre.name) })
                }
                4 -> {
                    FolderList(folders = folders, onFolderClick = { folder -> viewModel.filterByFolder(folder.path) })
                }
            }
        }
    }
}

@Composable
fun ArtistList(
    artists: List<Artist>,
    onArtistClick: (Artist) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(artists, key = { it.name }) { artist ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onArtistClick(artist) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = artist.name,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${artist.trackCount} songs • ${artist.albumCount} albums",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun GenreList(
    genres: List<Genre>,
    onGenreClick: (Genre) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(genres, key = { it.name }) { genre ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGenreClick(genre) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = genre.name.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = genre.name,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${genre.songCount} songs",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun FolderList(
    folders: List<Folder>,
    onFolderClick: (Folder) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(folders, key = { it.path }) { folder ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFolderClick(folder) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Folder,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = folder.name,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${folder.songCount} songs",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
