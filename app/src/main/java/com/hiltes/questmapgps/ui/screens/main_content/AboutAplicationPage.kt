package com.hiltes.questmapgps.ui.screens.main_content

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hiltes.questmapgps.ui.components.AppButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAppPage(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {

    val context = LocalContext.current
    val phone = "123456789"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("O Aplikacji",
            style = TextStyle(fontSize = 30.sp, textAlign= TextAlign.Justify),
            color = MaterialTheme.colorScheme.onPrimary,)
        Text("Aplikacja wspiera organizację i prowadzenie gier plenerowych i miejskich." +
                " Umożliwia uczestnikom wyszukiwanie punktów na mapie, korzystanie z podpowiedzi," +
                " kontakt z organizatorem oraz udostępnianie swojej lokalizacji w czasie rzeczywistym." +
                " Może być wykorzystywana do organizacji imprez rekreacyjnych, edukacyjnych i turystycznych." +
                "Aplikacja jest projektem z przedmiotu \"PUM\" na Uniwersytecie Rzeszowskim.",
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign= TextAlign.Justify,
            modifier = Modifier.padding(vertical = 20.dp)
        )
        Text("Autor: Dariusz Szymanek\n Wersja aplikacji: 0.3v",
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign= TextAlign.Center,
            modifier = Modifier.padding(vertical = 20.dp)
        )


        Text("O Bieżącej Grze",
            style = TextStyle(fontSize = 30.sp, textAlign= TextAlign.Justify),
            modifier = Modifier.padding(vertical = 30.dp),
            color = MaterialTheme.colorScheme.onPrimary,)

        Text("Gra która teraz jest załadowana ma charakter poglądowy i ma za cel pokazać obecnie" +
                " zaimplementowane możliwości aplikacji. Apka będzie rozwijana po oddaniu jej do oceny.",
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign= TextAlign.Justify,
            modifier = Modifier.padding(vertical = 20.dp)
        )


        Text(
            text = "Zadzwoń do organizatora Gry: $phone",
            style = TextStyle(fontStyle = Italic),
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.clickable {
                val intent = Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:$phone")
                )
                context.startActivity(intent)
            },
            textDecoration = TextDecoration.Underline
        )

        Spacer(modifier = Modifier.height(30.dp))

        AppButton(buttonText = "Wyloguj") {
            onLogout()
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Preview
@Composable
fun AboutAppPagePreview (){
    AboutAppPage({}, {})
}