package com.aceshot.musicplayer.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.aceshot.musicplayer.data.repository.SortOrder

@Composable
fun SortMenu(
    currentSort: SortOrder,
    onSortSelected: (SortOrder) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Rounded.Sort, contentDescription = "Sort")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Title") },
                onClick = { onSortSelected(SortOrder.TITLE); expanded = false },
                trailingIcon = if (currentSort == SortOrder.TITLE) { { Text("✓") } } else null
            )
            DropdownMenuItem(
                text = { Text("Artist") },
                onClick = { onSortSelected(SortOrder.ARTIST); expanded = false },
                trailingIcon = if (currentSort == SortOrder.ARTIST) { { Text("✓") } } else null
            )
            DropdownMenuItem(
                text = { Text("Date Added") },
                onClick = { onSortSelected(SortOrder.DATE_ADDED); expanded = false },
                trailingIcon = if (currentSort == SortOrder.DATE_ADDED) { { Text("✓") } } else null
            )
            DropdownMenuItem(
                text = { Text("Duration") },
                onClick = { onSortSelected(SortOrder.DURATION); expanded = false },
                trailingIcon = if (currentSort == SortOrder.DURATION) { { Text("✓") } } else null
            )
        }
    }
}
