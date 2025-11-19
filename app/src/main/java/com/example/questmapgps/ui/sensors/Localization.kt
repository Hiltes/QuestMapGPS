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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
    // ... (ta klasa pozostaje bez zmian)
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

// NOWA FUNKCJA POMOCNICZA DO WALIDACJI
/**
 * Sprawdza, czy podany przez użytkownika kod jest poprawny.
 * Ignoruje wielkość liter i białe znaki na początku/końcu.
 */
fun validateCode(userInput: String, correctCode: String): Boolean {
    // Normalizacja obu stringów - małe litery i usunięcie spacji
    val normalizedUserInput = userInput.trim().lowercase()
    val normalizedCorrectCode = correctCode.trim().lowercase()

    // Zwykłe porównanie
    return normalizedUserInput == normalizedCorrectCode
}


// ZMIENIONY KOMPONENT DIALOGU
@Composable
fun PointInfoDialog(
    pointData: PointData,
    onDismiss: () -> Unit,
    onCodeCorrect: (PointData) -> Unit // NOWY PARAMETR - callback po poprawnym kodzie
) {
    var userInputCode by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj") // Zmieniony tekst, bo "Zamknij" jest teraz mniej logiczne
            }
        },
        title = {
            Text(
                text = pointData.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            // Dodajemy scrollowanie, gdyby zawartość się nie mieściła
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()), // Umożliwia przewijanie
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Opis
                Column {
                    Text(text = "Opis:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(text = pointData.description, style = MaterialTheme.typography.bodyMedium)
                    Text(text= pointData.latitude.toString())
                    Text(text= pointData.longitude.toString())
                }

                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                // Wskazówka
                Column {
                    Text(text = "Wskazówka:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(text = pointData.hint, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic)
                }

                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                // ZMIENIONA SEKCJA "KOD"
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Kod", tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Wpisz kod:", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                    Spacer(Modifier.height(8.dp))

                    // Pole do wpisywania kodu
                    OutlinedTextField(
                        value = userInputCode,
                        onValueChange = { userInputCode = it },
                        label = { Text("Kod") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    // Przycisk do sprawdzania
                    Button(
                        onClick = {
                            if (validateCode(userInputCode, pointData.code)) {
                                // Poprawny kod
                                Toast.makeText(context, "✅ Poprawny kod!", Toast.LENGTH_SHORT).show()
                                onCodeCorrect(pointData) // Powiadom GamePage o sukcesie
                                onDismiss() // Zamknij dialog
                            } else {
                                // Błędny kod
                                Toast.makeText(context, "❌ Błędny kod, spróbuj ponownie.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Sprawdź")
                    }
                }
            }
        }
    )
}