package com.example.questmapgps

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.questmapgps.ui.navigation.NavGraph
import com.example.questmapgps.ui.navigation.Routes // NOWY IMPORT
import com.example.questmapgps.ui.screens.main_content.NotificationHelper
import com.example.questmapgps.ui.theme.QuestMapGPSTheme

class MainActivity : ComponentActivity() {

    private var pointIdToShow by mutableStateOf<Int?>(null)
    // NOWE: Stan do przechowywania trasy, do której mamy nawigować
    private var routeToNavigateTo by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)

        setContent {
            QuestMapGPSTheme {
                val navController = rememberNavController()
                // ZMIENIONE: Przekazujemy oba stany do NavGraph
                NavGraph(
                    navController = navController,
                    pointIdToShow = pointIdToShow,
                    onPointShown = { pointIdToShow = null },
                    routeToNavigateTo = routeToNavigateTo,
                    onNavigationHandled = { routeToNavigateTo = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        when (intent?.action) {
            NotificationHelper.ACTION_SHOW_POINT -> {
                val pointId = intent.getIntExtra(NotificationHelper.EXTRA_POINT_ID, -1)
                if (pointId != -1) {
                    this.pointIdToShow = pointId
                }
            }
            NotificationHelper.ACTION_NAVIGATE_TO_ABOUT -> {
                this.routeToNavigateTo = Routes.MAIN.ABOUT
            }
        }
    }
}