package com.example.questmapgps.ui.components
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.questmapgps.ui.theme.QuestMapGPSTheme


@Composable
fun AppButton(buttonText:String, operation: () -> Unit) {
    Button(onClick = operation,
        modifier = Modifier.padding(10.dp),
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(10.dp)
    ){
        Text(buttonText,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp)
        )
    }
}

@Composable
fun AppButtonSmall(buttonText:String, operation: () -> Unit) {
    Button(
        onClick = operation, // 👈 dodałem faktyczne użycie argumentu
        modifier = Modifier
            .padding(6.dp)
            .height(32.dp) // 👈 mniejsza wysokość
            .defaultMinSize(minHeight = 1.dp), // 👈 wyłącza domyślne 48dp
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp) // 👈 mniej miejsca w środku
    ) {
        Text(
            text = buttonText,
            color = MaterialTheme.colorScheme.tertiary, // zostawiłem Twój kolor
            style = MaterialTheme.typography.labelSmall // 👈 mniejsza czcionka
        )
    }
}

@Composable
fun SettingsButton(operation: () -> Unit) {
    IconButton(onClick = operation) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Lubię to",
            tint = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
fun HamburgerButton(operation: () -> Unit) {
    IconButton(onClick = operation) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
fun CloseButton(operation: () -> Unit) {
    IconButton(onClick = operation) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Menu",
            tint = MaterialTheme.colorScheme.tertiary
        )
    }
}


@Preview
@Composable
fun AppButtonPreview() {
    QuestMapGPSTheme {
        AppButton("Enter",{})
    }
}

@Preview
@Composable
fun SettingsButtonPreview() {
    QuestMapGPSTheme {
        SettingsButton({})
    }
}

@Preview
@Composable
fun HamburgerButtonPreview() {
    QuestMapGPSTheme {
        HamburgerButton({})
    }
}

@Preview
@Composable
fun CloseButtonPreview() {
    QuestMapGPSTheme {
        CloseButton({})
    }
}

@Preview
@Composable
fun AppButtonSmallPreview() {
    QuestMapGPSTheme {
        AppButtonSmall("EnterSmall",{})
    }
}