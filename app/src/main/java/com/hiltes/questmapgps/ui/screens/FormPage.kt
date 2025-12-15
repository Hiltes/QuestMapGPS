package com.hiltes.questmapgps.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hiltes.questmapgps.ui.components.AppButton
import com.hiltes.questmapgps.ui.components.AppLogo
import com.hiltes.questmapgps.ui.theme.QuestMapGPSTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormPage(
    onStart: (String) -> Unit,
    onExit: () -> Unit
) {
    var playerName by remember { mutableStateOf("") }
    var showNameAlert by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 60.dp)
    ) {
        AppLogo(width = 150, height = 150, padding = 8)

        Text(
            text = "QuestMapGPS",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 16.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = "Wpisz swoją nazwę",
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = playerName,
                    onValueChange = { playerName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.onTertiary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary,
                        cursorColor = MaterialTheme.colorScheme.tertiary,
                        focusedTextColor = MaterialTheme.colorScheme.tertiary,
                        unfocusedTextColor = MaterialTheme.colorScheme.tertiary
                    ),

                    )
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(top = 16.dp)
        ) {
            AppButton(buttonText = "Exit") { onExit() }
            AppButton(buttonText = "Start") {
                if(playerName.isBlank()){ // Sprawdzanie również pustych spacji
                    showNameAlert = true // Ustawienie flagi na true, aby pokazać alert
                } else {
                    onStart(playerName.trim()) // Usunięcie białych znaków na początku/końcu
                }
            }
        }
    }

    if (showNameAlert) {
        NameAlert(onDismiss = { showNameAlert = false }) // Przekazanie funkcji do zamknięcia
    }
}

@Composable
fun NameAlert(onDismiss: () -> Unit){
    AlertDialog(
        onDismissRequest = { onDismiss() }, // Użycie onDismiss
        title = { Text("Niepoprawna nazwa", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall, fontSize = 20.sp, textAlign = TextAlign.Center) },
        text = { Text("Musisz wpisać w polę swoją nazwę\nbez niej nie przejdziemy dalej...", modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp), color = MaterialTheme.colorScheme.onPrimary) },
        confirmButton = {
            TextButton(
                onClick = { onDismiss() }, // Zamknięcie dialogu po kliknięciu OK
                modifier = Modifier
                    .padding(6.dp)
                    .height(32.dp)
                    .defaultMinSize(minHeight = 1.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    "OK", // Zmieniono tekst na samo "OK"
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun FormPagePreview() {
    QuestMapGPSTheme {
        FormPage(
            onStart = {},
            onExit = {}
        )
    }
}