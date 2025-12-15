package com.hiltes.questmapgps.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hiltes.questmapgps.ui.screens.Main_Scaffold
import com.hiltes.questmapgps.ui.screens.FormPage
import com.hiltes.questmapgps.ui.screens.WelcomePage
import com.hiltes.questmapgps.ui.screens.main_content.GameViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    pointIdToShow: Int?,
    onPointShown: () -> Unit,
    routeToNavigateTo: String?,
    onNavigationHandled: () -> Unit
) {

    LaunchedEffect(routeToNavigateTo) {
        if (routeToNavigateTo != null) {
            // Sprawdzamy obecną trasę, aby uniknąć błędów
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (currentRoute != routeToNavigateTo) {
                navController.navigate(routeToNavigateTo)
            }
            onNavigationHandled()
        }
    }

    val gameViewModel: GameViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME,
        modifier = modifier
    ) {

        composable(Routes.WELCOME) {
            WelcomePage(
                gameViewModel = gameViewModel,
                onNavigateToFormPage = {
                    navController.navigate(Routes.FORM)
                },
                onNavigateToGamePage = {
                    navController.navigate(Routes.MAIN.GAME) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.FORM) {
            FormPage(
                onStart = { name ->
                    gameViewModel.saveUsername(name)
                    navController.navigate(Routes.MAIN.GAME) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                },
                onExit = { navController.popBackStack() }
            )
        }

        composable(Routes.MAIN.GAME) {
            Main_Scaffold(
                parentNav = navController,
                gameViewModel = gameViewModel,
                startDestination = Routes.MAIN.GAME
            )
        }


        composable(Routes.MAIN.ABOUT) {
            Main_Scaffold(
                parentNav = navController,
                gameViewModel = gameViewModel,
                startDestination = Routes.MAIN.ABOUT
            )
        }
    }
}