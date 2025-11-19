package com.example.questmapgps.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.questmapgps.ui.components.AppButtonSmall
import com.example.questmapgps.ui.components.FlashlightButton
import com.example.questmapgps.ui.components.InfoButton
import com.example.questmapgps.ui.components.LocalizeMeButton
import com.example.questmapgps.ui.components.SettingsButton
import com.example.questmapgps.ui.navigation.Routes
import com.example.questmapgps.ui.screens.main_content.AboutAppPage
import com.example.questmapgps.ui.screens.main_content.GamePage
import com.example.questmapgps.ui.screens.main_content.GameViewModel
import com.example.questmapgps.ui.screens.main_content.SettingsPage
import com.example.questmapgps.ui.theme.QuestMapGPSTheme

@Composable
fun Main_Scaffold(
    parentNav: NavHostController,
    gameViewModel: GameViewModel
) {
    val innerNavController = rememberNavController()

    QuestMapGPSTheme {
        Scaffold(
            topBar = {
                Topbar(
                    onNavigateGamePage = { innerNavController.navigate(Routes.MAIN.GAME) },
                    onNavigateToSettingsPage = { innerNavController.navigate(Routes.MAIN.SETTINGS) },
                    onNavigateAboutAppPage = { innerNavController.navigate(Routes.MAIN.ABOUT) }
                )
            }
        ) { innerPadding ->

            NavHost(
                navController = innerNavController,
                startDestination = Routes.MAIN.GAME,
                modifier = Modifier.padding(innerPadding)
            ) {

                composable(Routes.MAIN.GAME) {
                    GamePage(
                        gameViewModel = gameViewModel,
                        onNavigateBack = { parentNav.popBackStack() }
                    )
                }

                composable(Routes.MAIN.SETTINGS) {
                    SettingsPage(
                        onNavigateBack = { innerNavController.popBackStack() }
                    )
                }

                composable(Routes.MAIN.ABOUT) {
                    AboutAppPage(
                        onNavigateBack = { innerNavController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
fun Topbar(
    onNavigateGamePage: () -> Unit,
    onNavigateToSettingsPage: () -> Unit,
    onNavigateAboutAppPage: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Default.Close else Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Text(
                text = "Alpha Version",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )

            SettingsButton { onNavigateToSettingsPage() }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppButtonSmall("Strona Główna", onNavigateGamePage) {}
                AppButtonSmall("Informacje", onNavigateAboutAppPage) {}
            }
        }
    }
}

@Composable
fun BottomBar(
    modifier: Modifier,
    onLocalizeMeClick: () -> Unit,
) {
    Row(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            FlashlightButton(5)

            LocalizeMeButton(
                operation = onLocalizeMeClick,
                padding = 5
            )

            InfoButton(padding = 10, phone = "123456789")

        }
    }
}
