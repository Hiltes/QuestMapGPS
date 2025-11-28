package com.example.questmapgps.ui.sensors

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.questmapgps.ui.theme.Black
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationHelper(context: Context) {
    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val applicationContext: Context = context.applicationContext

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Pair<Double, Double>? {
        val hasPermission = ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            Log.w("LocationHelper", "Brak uprawnień do lokalizacji podczas próby jej pobrania.")
            return null
        }
        return suspendCancellableCoroutine { continuation ->
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setWaitForAccurateLocation(true)
                .setMaxUpdates(1)
                .build()
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null && continuation.isActive) {
                        Log.d("LocationHelper", "Sukces! Otrzymano nową lokalizację: ${location.latitude}, ${location.longitude}")
                        continuation.resume(Pair(location.latitude, location.longitude))
                    } else if (continuation.isActive) {
                        Log.w("LocationHelper", "Otrzymano pusty wynik lokalizacji.")
                        continuation.resume(null)
                    }
                    fusedLocationProviderClient.removeLocationUpdates(this)
                }
                override fun onLocationAvailability(availability: LocationAvailability) {
                    if (!availability.isLocationAvailable && continuation.isActive) {
                        Log.e("LocationHelper", "Lokalizacja jest niedostępna (np. w budynku, tunelu).")
                        continuation.resume(null)
                        fusedLocationProviderClient.removeLocationUpdates(this)
                    }
                }
            }
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            continuation.invokeOnCancellation {
                Log.d("LocationHelper", "Anulowano żądanie lokalizacji.")
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            }
        }
    }
}

fun validateCode(userInput: String, correctCode: String): Boolean {
    return userInput.trim().lowercase() == correctCode.trim().lowercase()
}

@Composable
fun PointInfoDialog(
    pointData: PointData,
    onDismiss: () -> Unit,
    isSolved: Boolean,
    onCodeCorrect: (PointData) -> Unit
) {
    var userInputCode by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.secondary,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = pointData.name,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Opis",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        pointData.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                HorizontalDivider()

                Column {
                    Text(
                        "Wskazówka",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        pointData.hint,
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                HorizontalDivider()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.secondary,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isSolved) Icons.Default.Check else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (isSolved) Color.Green else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (isSolved) "Zadanie wykonane" else "Wpisz kod",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = if (isSolved) pointData.code else userInputCode,
                        onValueChange = { userInputCode = it },
                        label = { Text("Kod") },
                        // BLOKADA POLA
                        enabled = !isSolved,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodySmall,
                    )

                    Spacer(Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (validateCode(userInputCode, pointData.code)) {
                                Toast.makeText(context, "Poprawny kod!", Toast.LENGTH_SHORT).show()
                                onCodeCorrect(pointData)
                                onDismiss()
                            } else {
                                Toast.makeText(context, "Błędny kod.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !isSolved,
                        modifier = Modifier
                            .height(32.dp)
                            .defaultMinSize(minHeight = 1.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            // ZMIANA KOLORU NA SZARY JEŚLI ROZWIĄZANE
                            containerColor = if (isSolved) Color.Gray else MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isSolved) "ROZWIĄZANO" else "Sprawdź",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .padding(6.dp)
                    .height(32.dp)
                    .defaultMinSize(minHeight = 1.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("Zamknij", style = MaterialTheme.typography.labelSmall)
            }
        }
    )
}
