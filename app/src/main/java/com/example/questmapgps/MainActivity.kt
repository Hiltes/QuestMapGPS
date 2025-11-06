package com.example.questmapgps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.questmapgps.ui.navigation.NavGraph
import com.example.questmapgps.ui.theme.QuestMapGPSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            QuestMapGPSTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
