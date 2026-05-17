package com.aceshot.musicplayer.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aceshot.musicplayer.data.preferences.ThemeMode
import com.aceshot.musicplayer.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val minDuration by viewModel.minDurationFilterMs.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text("Theme", style = MaterialTheme.typography.titleMedium)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            ThemeMode.values().forEach { mode ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 16.dp)) {
                    RadioButton(
                        selected = themeMode == mode,
                        onClick = { viewModel.setThemeMode(mode) }
                    )
                    Text(mode.name)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Scanner Filter", style = MaterialTheme.typography.titleMedium)
        Text("Hide audio files shorter than ${minDuration / 1000} seconds", style = MaterialTheme.typography.bodyMedium)
        Slider(
            value = (minDuration / 1000).toFloat(),
            onValueChange = { viewModel.setMinDurationFilter((it * 1000).toLong()) },
            valueRange = 0f..120f,
            steps = 11
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.rescanLibrary() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Rounded.Sync, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Rescan Library")
        }
    }
}
