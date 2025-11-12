package com.example.questmapgps.ui.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.questmapgps.ui.sensors.rememberAmbientLightLevel

private val DarkColorScheme = darkColorScheme(
    primary = LightBlue,
    secondary = DarkBlue,
    tertiary = Black,
    onPrimary = Sandy,
    background = DarkBlue,
    onBackground = Sandy,
    onTertiary = Sandy

)

private val LightColorScheme = lightColorScheme(
    primary = Sandy,
    secondary = Olive,
    tertiary = Black,
    onTertiary = White,
    onPrimary = Black,
    background = Olive,
    onBackground = Olive

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)
private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(10.dp),
    large = RoundedCornerShape(12.dp),
    extraLarge = RoundedCornerShape(16.dp)
)


@Composable
fun QuestMapGPSTheme(
    dynamicColor: Boolean = false,
    useAmbientSensor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val ambientLight = if (useAmbientSensor) rememberAmbientLightLevel() else 1000f

    val isDarkBySensor = ambientLight < 30f
    val targetDarkTheme = if (useAmbientSensor) isDarkBySensor else isSystemInDarkTheme()


    var darkTheme by remember { mutableStateOf(targetDarkTheme) }
    LaunchedEffect(targetDarkTheme) {

        kotlinx.coroutines.delay(250)
        darkTheme = targetDarkTheme
    }
    val baseScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val animatedColorScheme = baseScheme.copy(
        primary = animateColorAsState(baseScheme.primary, label = "primary").value,
        secondary = animateColorAsState(baseScheme.secondary, label = "secondary").value,
        background = animateColorAsState(baseScheme.background, label = "background").value,
        onBackground = animateColorAsState(baseScheme.onBackground, label = "onBackground").value,
        surface = animateColorAsState(baseScheme.surface, label = "surface").value,
        onSurface = animateColorAsState(baseScheme.onSurface, label = "onSurface").value
    )

    MaterialTheme(
        colorScheme = animatedColorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}