package com.example.questmapgps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.questmapgps.ui.navigation.NavGraph
import com.example.questmapgps.ui.navigation.Routes
import com.example.questmapgps.ui.screens.main_content.NotificationHelper
import com.example.questmapgps.ui.theme.QuestMapGPSTheme

class MainActivity : ComponentActivity() {

    private var pointIdToShow by mutableStateOf<Int?>(null)
    private var routeToNavigateTo by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val requestNotificationPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {

                    Log.d("MainActivity", "Permission granted")
                } else {
                    Log.w("MainActivity", "User denied POST_NOTIFICATIONS permission")
                }
            }

        handleIntent(intent)

        // 2. Potem sprawdź uprawnienia
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            QuestMapGPSTheme {
                val navController = rememberNavController()
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
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return

        when (intent.action) {
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