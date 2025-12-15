package com.hiltes.questmapgps.ui.screens.main_content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsPage(
    onNavigateBack: () -> Unit,
    vm: RankingViewModel = viewModel()
) {
    val users by vm.users.collectAsState()

    Column(Modifier.fillMaxSize()) {

        Text(
            text = "Ranking",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )

        LazyColumn(Modifier.weight(1f)) {
            itemsIndexed(
                items = users,
                key = { i, user -> "${user.username}_$i" }
            ) { i, user ->
                RankingItem(
                    position = i + 1,
                    name = user.username,
                    points = user.points
                )
            }
        }

        Button(
            onClick = { vm.loadNextPage() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Załaduj więcej")
        }
    }
}

@Composable
fun RankingItem(position: Int, name: String, points: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("$position. $name", color=MaterialTheme.colorScheme.onPrimary)
        Text("$points pkt", color=MaterialTheme.colorScheme.onPrimary)
    }
}