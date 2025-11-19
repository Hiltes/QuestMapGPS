package com.example.questmapgps.ui.data


data class UserData(
    val username: String = "",
    val points: Int = 0,
    val visitedPoints: List<String> = emptyList(),
    val codesSolvedPoints: List<String> = emptyList(),
    val solvedPoints: List<String> = emptyList()
)