package com.example.questmapgps.ui.screens


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.questmapgps.ui.components.AppButtonSmall
import com.example.questmapgps.ui.components.FlashlightButton
import com.example.questmapgps.ui.components.InfoButton
import com.example.questmapgps.ui.components.SettingsButton
import com.example.questmapgps.ui.navigation.Routes
import com.example.questmapgps.ui.screens.main_content.AboutAppPage
import com.example.questmapgps.ui.screens.main_content.GamePage
import com.example.questmapgps.ui.screens.main_content.SettingsPage
import com.example.questmapgps.ui.theme.QuestMapGPSTheme


@Composable
fun Main_Scaffold(
    startDestination: String = Routes.GAME,
    parentNav: NavHostController
) {
    val innerNavController = rememberNavController()

    Scaffold(
        topBar = {
            Topbar(
                onNavigateGamePage = { innerNavController.navigate(Routes.GAME) },
                onNavigateToSettingsPage = { innerNavController.navigate(Routes.SETTINGS) },
                onNavigateAboutAppPage = { innerNavController.navigate(Routes.ABOUT) },
            )
        },
        bottomBar = {
            BottomBar()
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.GAME) {
                GamePage(
                    onNavigateBack = { parentNav.navigate(Routes.WELCOME) }
                )
            }
            composable(Routes.SETTINGS) {
                SettingsPage(
                    onNavigateBack = { innerNavController.popBackStack() }
                )
            }
            composable(Routes.ABOUT) {
                AboutAppPage(
                    onNavigateBack = { innerNavController.popBackStack() }
                )
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
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }

            Text(
                text = "Nazwa Gry",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
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
                AppButtonSmall("Strona Główna") { onNavigateGamePage() }
                AppButtonSmall("O Aplikacji") { onNavigateAboutAppPage() }
            }
        }
    }
}

@Composable
fun BottomBar() {
    Row(modifier =
        Modifier
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.End
    ){
        Column(modifier = Modifier.padding(vertical = 10.dp)) {
            FlashlightButton({},5)
            InfoButton({},5)
        }

    }
}




@Preview
@Composable
fun TopbarPreview(){
    QuestMapGPSTheme {
        Topbar({},{},{})
    }
}

@Preview
@Composable
fun BottomBarPreview() {
    QuestMapGPSTheme {
        BottomBar()
    }
}