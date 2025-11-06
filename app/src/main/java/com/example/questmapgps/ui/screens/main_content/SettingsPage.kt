package com.example.questmapgps.ui.screens.main_content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun SettingsPage(onNavigateBack: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("To jest strona ustawień")
    }
}