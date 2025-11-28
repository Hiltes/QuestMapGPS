package com.example.questmapgps.ui.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
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
    val systemDarkTheme = isSystemInDarkTheme()

    val ambientLight = if (useAmbientSensor) rememberAmbientLightLevel() else 1000f

    var useDarkTheme by remember { mutableStateOf(systemDarkTheme) }

    if (useAmbientSensor) {

        if (useDarkTheme && ambientLight > 80f) {
            useDarkTheme = false
        } else if (!useDarkTheme && ambientLight < 30f) {
            useDarkTheme = true
        }
    } else {
        useDarkTheme = systemDarkTheme
    }


    val targetScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }


    val animatedScheme = rememberAnimatedColorScheme(
        targetScheme = targetScheme,
        animationSpec = tween(durationMillis = 600)
    )

    MaterialTheme(
        colorScheme = animatedScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}

@Composable
private fun rememberAnimatedColorScheme(
    targetScheme: ColorScheme,
    animationSpec: AnimationSpec<Color>
): ColorScheme {
    val primary by animateColorAsState(targetScheme.primary, animationSpec, label = "primary")
    val onPrimary by animateColorAsState(targetScheme.onPrimary, animationSpec, label = "onPrimary")
    val primaryContainer by animateColorAsState(targetScheme.primaryContainer, animationSpec, label = "primaryContainer")
    val onPrimaryContainer by animateColorAsState(targetScheme.onPrimaryContainer, animationSpec, label = "onPrimaryContainer")

    val secondary by animateColorAsState(targetScheme.secondary, animationSpec, label = "secondary")
    val onSecondary by animateColorAsState(targetScheme.onSecondary, animationSpec, label = "onSecondary")
    val secondaryContainer by animateColorAsState(targetScheme.secondaryContainer, animationSpec, label = "secondaryContainer")
    val onSecondaryContainer by animateColorAsState(targetScheme.onSecondaryContainer, animationSpec, label = "onSecondaryContainer")

    val tertiary by animateColorAsState(targetScheme.tertiary, animationSpec, label = "tertiary")
    val onTertiary by animateColorAsState(targetScheme.onTertiary, animationSpec, label = "onTertiary")
    val tertiaryContainer by animateColorAsState(targetScheme.tertiaryContainer, animationSpec, label = "tertiaryContainer")
    val onTertiaryContainer by animateColorAsState(targetScheme.onTertiaryContainer, animationSpec, label = "onTertiaryContainer")

    val background by animateColorAsState(targetScheme.background, animationSpec, label = "background")
    val onBackground by animateColorAsState(targetScheme.onBackground, animationSpec, label = "onBackground")

    val surface by animateColorAsState(targetScheme.surface, animationSpec, label = "surface")
    val onSurface by animateColorAsState(targetScheme.onSurface, animationSpec, label = "onSurface")
    val surfaceVariant by animateColorAsState(targetScheme.surfaceVariant, animationSpec, label = "surfaceVariant")
    val onSurfaceVariant by animateColorAsState(targetScheme.onSurfaceVariant, animationSpec, label = "onSurfaceVariant")

    val error by animateColorAsState(targetScheme.error, animationSpec, label = "error")
    val onError by animateColorAsState(targetScheme.onError, animationSpec, label = "onError")
    val errorContainer by animateColorAsState(targetScheme.errorContainer, animationSpec, label = "errorContainer")
    val onErrorContainer by animateColorAsState(targetScheme.onErrorContainer, animationSpec, label = "onErrorContainer")

    val outline by animateColorAsState(targetScheme.outline, animationSpec, label = "outline")

    return remember(targetScheme) {
        targetScheme.copy(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = primaryContainer,
            onPrimaryContainer = onPrimaryContainer,
            secondary = secondary,
            onSecondary = onSecondary,
            secondaryContainer = secondaryContainer,
            onSecondaryContainer = onSecondaryContainer,
            tertiary = tertiary,
            onTertiary = onTertiary,
            tertiaryContainer = tertiaryContainer,
            onTertiaryContainer = onTertiaryContainer,
            background = background,
            onBackground = onBackground,
            surface = surface,
            onSurface = onSurface,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = onSurfaceVariant,
            error = error,
            onError = onError,
            errorContainer = errorContainer,
            onErrorContainer = onErrorContainer,
            outline = outline
        )
    }.copy(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        tertiary = tertiary,
        onTertiary = onTertiary,
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = onTertiaryContainer,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        error = error,
        onError = onError,
        errorContainer = errorContainer,
        onErrorContainer = onErrorContainer,
        outline = outline
    )
}