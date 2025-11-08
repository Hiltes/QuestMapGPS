package com.example.questmapgps.ui.sensors

// Dodaj na początku pliku (przed @Composable fun GamePage)
data class PointData(
    val name: String,
    val description: String,
    val hint: String,
    val code: String
)