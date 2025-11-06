package com.example.questmapgps.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.questmapgps.ui.screens.AboutAppPage
import com.example.questmapgps.ui.screens.FormPage
import com.example.questmapgps.ui.screens.GamePage
import com.example.questmapgps.ui.screens.SettingsPage
import com.example.questmapgps.ui.screens.WelcomePage

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME,
        modifier = modifier
    ) {
        composable(Routes.WELCOME) {
            WelcomePage(
                onNavigateToFormPage = { navController.navigate(Routes.FORM) }
            )
        }
        composable(Routes.GAME) {
            GamePage(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsPage(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Routes.ABOUT) {
            AboutAppPage(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Routes.FORM) {
            FormPage(
                onStart = { name ->
                    navController.navigate(Routes.GAME)
                },
                onExit = {
                    navController.popBackStack()
                }
            )
        }
    }
}