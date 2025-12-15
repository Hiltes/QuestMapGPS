package com.hiltes.questmapgps.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hiltes.questmapgps.ui.components.AppButton
import com.hiltes.questmapgps.ui.components.AppLogo
import com.hiltes.questmapgps.ui.screens.main_content.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun WelcomePage(
    gameViewModel: GameViewModel,
    onNavigateToFormPage: () -> Unit,
    onNavigateToGamePage: () -> Unit
) {
    val context = LocalContext.current


    val userData by gameViewModel.userData.collectAsState()


    LaunchedEffect(userData) {
        if (userData != null && userData!!.username.isNotBlank()) {
            delay(100)
            onNavigateToGamePage()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppLogo(width = 200, height = 200, padding = 5)
        Text(
            "QuestMapGPS",
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .size(100.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            "Witaj!\nRozpocznij swoją przygodę...",
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary
        )


        AppButton("Enter") {
            onNavigateToFormPage()
        }
    }
}