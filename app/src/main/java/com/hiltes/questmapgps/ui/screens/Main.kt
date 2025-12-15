package com.hiltes.questmapgps.ui.screens

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
import com.hiltes.questmapgps.ui.components.AppButtonSmall
import com.hiltes.questmapgps.ui.components.ClusteringButton
import com.hiltes.questmapgps.ui.components.FlashlightButton
import com.hiltes.questmapgps.ui.components.InfoButton
import com.hiltes.questmapgps.ui.components.LocalizeMeButton
import com.hiltes.questmapgps.ui.components.SettingsButton
import com.hiltes.questmapgps.ui.navigation.Routes
import com.hiltes.questmapgps.ui.screens.main_content.AboutAppPage
import com.hiltes.questmapgps.ui.screens.main_content.GamePage
import com.hiltes.questmapgps.ui.screens.main_content.GameViewModel
import com.hiltes.questmapgps.ui.screens.main_content.SettingsPage
import com.hiltes.questmapgps.ui.theme.QuestMapGPSTheme

@Composable
fun Main_Scaffold(
    parentNav: NavHostController,
    gameViewModel: GameViewModel,
    startDestination: String = Routes.MAIN.GAME
) {
    val innerNavController = rememberNavController()

    QuestMapGPSTheme {
        Scaffold(
            topBar = {
                Topbar(
                    onNavigateGamePage = {
                        // Zabezpieczenie przed wielokrotnym dodawaniem na stos
                        innerNavController.navigate(Routes.MAIN.GAME) {
                            popUpTo(innerNavController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    onNavigateToSettingsPage = { innerNavController.navigate(Routes.MAIN.SETTINGS) },
                    onNavigateAboutAppPage = { innerNavController.navigate(Routes.MAIN.ABOUT) }
                )
            }
        ) { innerPadding ->

            NavHost(
                navController = innerNavController,
                startDestination = startDestination, // Używamy parametru tutaj
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
                        onNavigateBack = { innerNavController.popBackStack() },
                        onLogout = {
                            gameViewModel.logout()
                            parentNav.navigate(Routes.WELCOME) {
                                popUpTo(Routes.WELCOME) { inclusive = true }
                            }
                        }
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
    modifier: Modifier = Modifier,
    onLocalizeMeClick: () -> Unit,
    clustering: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp),
        contentAlignment = Alignment.BottomEnd
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.End
        ) {
            ClusteringButton(clustering)
            FlashlightButton()
            LocalizeMeButton(onLocalizeMeClick)
            InfoButton(phone = "123456789")
        }
    }
}