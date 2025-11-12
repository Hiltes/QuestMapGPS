package com.example.questmapgps.ui.components
import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.questmapgps.ui.theme.QuestMapGPSTheme
import compose.icons.FeatherIcons
import compose.icons.feathericons.Lock
import compose.icons.feathericons.Unlock
import compose.icons.feathericons.Zap
import compose.icons.feathericons.ZapOff
import io.github.dellisd.spatialk.geojson.Position
import kotlin.time.Duration.Companion.seconds


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
fun InfoButton(operation: () -> Unit, padding: Int){
    IconButton(
        onClick = operation,
        modifier = Modifier
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            )
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = CircleShape
            )
            .padding(padding.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Pomoc i informacje",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}
//@Composable
//fun LockTrackingButton(operation: () -> Unit, padding: Int, isLocked: Boolean, modifier: Modifier){
//    IconButton(
//        onClick = operation,
//        modifier = Modifier
//            .border(
//                width = 2.dp,
//                color = MaterialTheme.colorScheme.onPrimary,
//                shape = CircleShape
//            )
//            .background(
//                color = MaterialTheme.colorScheme.background,
//                shape = CircleShape
//            )
//            .padding(padding.dp)
//    ) {
//        if(isLocked){
//        Icon(
//            imageVector = FeatherIcons.Unlock,
//            contentDescription = "Sledzenie lokalizacji",
//            tint = MaterialTheme.colorScheme.onPrimary
//        )} else {
//            Icon(
//                imageVector = FeatherIcons.Lock,
//                contentDescription = "Sledzenie lokalizacji",
//                tint = MaterialTheme.colorScheme.onPrimary
//            )}
//        }
//    }


@Composable
fun FlashlightButton(padding: Int) {
    val context = LocalContext.current
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraId = remember {
        cameraManager.cameraIdList.firstOrNull() ?: ""
    }

    var isTorchOn by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            try {
                cameraManager.setTorchMode(cameraId, !isTorchOn)
                isTorchOn = !isTorchOn
            } catch (e: Exception) {
                e.printStackTrace()
            }
        },
        modifier = Modifier
            .padding(padding.dp)
            .clip(CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.onPrimary, CircleShape)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Icon(
            imageVector = if (isTorchOn) FeatherIcons.Zap else FeatherIcons.ZapOff,
            contentDescription = if (isTorchOn) "Wyłącz latarkę" else "Włącz latarkę",
            tint = if (isTorchOn)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onPrimary
        )
    }
}
@Composable
fun AppButtonSmall(buttonText:String, operation: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = operation,
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
            text = buttonText,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun SettingsButton(operation: () -> Unit) {
    IconButton(onClick = operation) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Lubię to",
            tint = MaterialTheme.colorScheme.onPrimary
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

@Composable
fun LocalizeMeButton(operation: () -> Unit, padding: Int){
    IconButton(
        onClick = operation,
        modifier = Modifier
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            )
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = CircleShape
            )
            .padding(padding.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Pomoc i informacje",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}
//val local: Boolean = true
//@Preview
//@Composable
//fun LockTrackingButtonPreview() {
//    QuestMapGPSTheme {
//        LockTrackingButton({ },5, isLocked = local, modifier = Modifier )
//    }
//}


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

@Preview
@Composable
fun InfoButtonPreview() {
    QuestMapGPSTheme {
        InfoButton({},10)
    }
}



@Preview
@Composable
fun FlashlightButtonPreview() {
    QuestMapGPSTheme {
        FlashlightButton(10)
    }
}