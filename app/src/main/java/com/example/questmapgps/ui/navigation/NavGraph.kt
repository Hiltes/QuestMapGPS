package com.example.questmapgps.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.questmapgps.ui.screens.Main_Scaffold
import com.example.questmapgps.ui.screens.FormPage
import com.example.questmapgps.ui.screens.WelcomePage
import com.example.questmapgps.ui.screens.main_content.GameViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    pointIdToShow: Int?,
    onPointShown: () -> Unit,
    routeToNavigateTo: String?,
    onNavigationHandled: () -> Unit
) {
    // Obsługa zewnętrznych poleceń nawigacji
    LaunchedEffect(routeToNavigateTo) {
        if (routeToNavigateTo != null) {
            navController.navigate(routeToNavigateTo)
            onNavigationHandled()
        }
    }

    val gameViewModel: GameViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME,
        modifier = modifier
    ) {

        // EKRAN POWITALNY
        composable(Routes.WELCOME) {
            WelcomePage(
                onNavigateToFormPage = { navController.navigate(Routes.FORM) }
            )
        }

        // FORMULARZ
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
                gameViewModel = gameViewModel
            )
        }
    }
}
