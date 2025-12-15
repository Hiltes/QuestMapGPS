package com.hiltes.questmapgps.ui.components
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hiltes.questmapgps.ui.theme.QuestMapGPSTheme
import compose.icons.FeatherIcons
import compose.icons.feathericons.Zap
import compose.icons.feathericons.ZapOff
import androidx.core.net.toUri


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
fun InfoButton(phone: String) {
    var showDialog by remember { mutableStateOf(false) }

    IconButton(
        onClick = { showDialog = true },
        modifier = Modifier
            .size(64.dp) // stały rozmiar
            .clip(CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.onPrimary, CircleShape)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Informacje",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }

    if (showDialog) {
        InfoDialog(phone = phone, onDismiss = { showDialog = false })
    }
}

@Composable
fun InfoDialog(phone: String, onDismiss: () -> Unit) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Informacje o grze",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                "Jeśli masz jakiś problem podczas gry możesz zadzwonić do organizatora.",
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        confirmButton = {
            AppButtonSmall("Zadzwoń", operation = {val intent = Intent(
                Intent.ACTION_DIAL,
                "tel:$phone".toUri()
            )
                context.startActivity(intent)
                onDismiss()}, modifier = Modifier) {}
        },
        dismissButton = {

            AppButtonSmall("Zamknij", onDismiss) {}
        }
    )
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
fun FlashlightButton() {
    val context = LocalContext.current
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraId = remember { cameraManager.cameraIdList.firstOrNull() ?: "" }

    var isTorchOn by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            try {
                cameraManager.setTorchMode(cameraId, !isTorchOn)
                isTorchOn = !isTorchOn
            } catch (_: Exception) {}
        },
        modifier = Modifier
            .size(48.dp)  // STAŁY ROZMIAR PRZYCISKU
            .clip(CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.onPrimary, CircleShape)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Icon(
            imageVector = if (isTorchOn) FeatherIcons.Zap else FeatherIcons.ZapOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}





@Composable
fun AppButtonSmall(
    buttonText: String,
    operation: () -> Unit,
    modifier: Modifier = Modifier,
    function: () -> Unit
) {
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
            imageVector = Icons.Default.Star,
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
fun LocalizeMeButton(operation: () -> Unit) {
    IconButton(
        onClick = operation,
        modifier = Modifier
            .size(48.dp)  // stały wymiar
            .clip(CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.onPrimary, CircleShape)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Lokalizuj mnie",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun ClusteringButton(operation: () -> Unit) {
    IconButton(
        onClick = operation,
        modifier = Modifier
            .size(48.dp)  // stały wymiar
            .clip(CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.onPrimary, CircleShape)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Icon(
            imageVector = Icons.Default.Build,
            contentDescription = "Włącz/wyłącz Clustering",
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
        AppButton("Enter") {}
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
        AppButtonSmall("EnterSmall", {}) {}
    }
}

@Preview
@Composable
fun InfoButtonPreview() {
    QuestMapGPSTheme {
        InfoButton(phone = "123456789")

    }
}



@Preview
@Composable
fun FlashlightButtonPreview() {
    QuestMapGPSTheme {
        FlashlightButton()
    }
}