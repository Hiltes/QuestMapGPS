package com.example.questmapgps.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.questmapgps.ui.screens.Main_Scaffold
import com.example.questmapgps.ui.screens.FormPage
import com.example.questmapgps.ui.screens.WelcomePage

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME,
        modifier = modifier
    ) {
        //  Ekran powitalny
        composable(Routes.WELCOME) {
            WelcomePage(
                onNavigateToFormPage = { navController.navigate(Routes.FORM) }
            )
        }

        //  Formularz
        composable(Routes.FORM) {
            FormPage(
                onStart = { name ->
                    navController.navigate(Routes.GAME) {
                        popUpTo(Routes.WELCOME) { inclusive = true } // usuwa poprzednie ekrany ze stosu
                    }
                },
                onExit = { navController.popBackStack() }
            )
        }

        // Wszystkie pozostałe strony (game, settings, about)
        composable(Routes.GAME) {
            Main_Scaffold(startDestination = Routes.GAME, parentNav = navController)
        }
        composable(Routes.SETTINGS) {
            Main_Scaffold(startDestination = Routes.SETTINGS, parentNav = navController)
        }
        composable(Routes.ABOUT) {
            Main_Scaffold(startDestination = Routes.ABOUT, parentNav = navController)
        }
    }
}
