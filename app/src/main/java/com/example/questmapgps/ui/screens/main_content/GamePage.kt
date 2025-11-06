package com.example.questmapgps.ui.screens.main_content

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.questmapgps.ui.theme.QuestMapGPSTheme


@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun GamePage(onNavigateBack: () -> Unit) {
    QuestMapGPSTheme {
        Text("SIEMA")
    }
}