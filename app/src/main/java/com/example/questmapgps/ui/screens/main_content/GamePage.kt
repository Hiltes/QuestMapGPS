package com.example.questmapgps.ui.screens.main_content

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.questmapgps.ui.sensors.LocationHelper
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.style.BaseStyle
import androidx.compose.ui.graphics.Color
import com.example.questmapgps.ui.components.LockTrackingButton
import com.example.questmapgps.ui.screens.BottomBar
import com.example.questmapgps.ui.sensors.PointInfoDialog
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.util.ClickResult
import java.io.File
import kotlin.time.Duration.Companion.seconds
import io.github.dellisd.spatialk.geojson.Feature
import io.github.dellisd.spatialk.geojson.FeatureCollection
import io.github.dellisd.spatialk.geojson.Point


// Data class dla danych punktu
data class PointData(
    val name: String,
    val description: String,
    val hint: String,
    val code: String
)

@Composable
fun GamePage(onNavigateBack: () -> Unit) {

    Box(modifier = Modifier.fillMaxSize()
    ) {

        var lock by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val locationHelper = remember { LocationHelper(context) }
        var location by remember { mutableStateOf<Pair<Double, Double>?>(null) }

        var hasPermission by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
        }

        var file by remember { mutableStateOf<File?>(null) }
        var loadError by remember { mutableStateOf<String?>(null) }

        // ✅ Stan dla wybranego punktu
        var selectedPoint by remember { mutableStateOf<PointData?>(null) }

        val camera = rememberCameraState(
            firstPosition = CameraPosition(
                target = Position(22.003122228653037, 50.03120384606565),
                zoom = 13.0
            )
        )




        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            hasPermission = isGranted
            if (!isGranted) {
                Log.w("GamePage", "Pozwolenie na lokalizację odrzucone")
            }
        }



        LaunchedEffect(Unit) {
            if (!hasPermission) {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        LaunchedEffect(Unit) {
            try {
                withContext(Dispatchers.IO) {
                    val f = File(context.cacheDir, "map.geojson")
                    if (!f.exists()) {
                        Log.d("GamePage", "Kopiowanie map.geojson z assets...")
                        context.assets.open("map.geojson").use { input ->
                            f.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                    file = f
                    Log.d("GamePage", "GeoJSON załadowany: ${f.absolutePath}")
                }
            } catch (e: Exception) {
                loadError = "Błąd ładowania mapy: ${e.message}"
                Log.e("GamePage", "Błąd ładowania GeoJSON", e)
            }
        }

        LaunchedEffect(hasPermission) {
            if (hasPermission) {
                while (isActive) {
                    try {
                        val newLocation = locationHelper.getCurrentLocation()
                        if (newLocation != null) {
                            location = newLocation
                            Log.d("GamePage", "Nowa lokalizacja: $newLocation")
                            if(lock) {
                            camera.animateTo(
                                finalPosition = camera.position.copy(
                                    target = Position(newLocation.second, newLocation.first)
                                ),
                                duration = 3.seconds,
                            )}
                        } else {
                            Log.w("GamePage", "Nie udało się pobrać lokalizacji")
                        }
                    } catch (e: Exception) {
                        Log.e("GamePage", "Błąd pobierania lokalizacji", e)
                    }
                    delay(3_000)
                }
            }
        }

        // ✅ Wyświetl dialog gdy punkt jest wybrany
        selectedPoint?.let { point ->
            PointInfoDialog(
                pointData = point,
                onDismiss = { selectedPoint = null }
            )
        }

        when {
            loadError != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("❌ Błąd: $loadError")
                }
            }

            file == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                MaplibreMap(
                    cameraState = camera,
                    baseStyle = BaseStyle.Uri(
                        "https://api.maptiler.com/maps/basic-v2/style.json?key=3EzNiP9jPuozp4ZM6TiX"
                    )
                ) {
                    // Źródło twojej mapy GeoJSON
                    val myGeoJsonSource = rememberGeoJsonSource(
                        data = GeoJsonData.Uri(file!!.toUri().toString())
                    )

                    // Czerwone punkty z map.geojson
                    CircleLayer(
                        id = "my-geojson-points",
                        source = myGeoJsonSource,
                        color = const(Color.Red),
                        radius = const(6.dp),
                        onClick = { features ->
                            try {
                                val feature = features.firstOrNull()
                                if (feature != null) {
                                    val jsonObject = JSONObject(feature.json())
                                    val properties = jsonObject.getJSONObject("properties")

                                    val pointData = PointData(
                                        name = properties.getString("name"),
                                        description = properties.getString("description"),
                                        hint = properties.getString("hint"),
                                        code = properties.getString("code")
                                    )
                                    selectedPoint = pointData
                                    Log.d("GamePage", "Kliknięto punkt: ${pointData.name}")
                                }
                            } catch (e: Exception) {
                                Log.e("GamePage", "Błąd parsowania punktu", e)
                            }
                            ClickResult.Consume
                        }
                    )

                    // 🔵 Dodajemy niebieski znacznik użytkownika
                    // 🔵 Dodajemy niebieski znacznik użytkownika
                    if (location != null) {
                        val userFeatureCollection = FeatureCollection(
                            features = listOf(
                                Feature(
                                    // ✅ Poprawka: opakowujemy współrzędne w obiekt Position
                                    geometry = Point(
                                        Position(
                                            longitude = location!!.second,
                                            latitude = location!!.first
                                        )
                                    ),
                                    properties = emptyMap()
                                )
                            )
                        )

                        val userSource = rememberGeoJsonSource(
                            data = GeoJsonData.Features(userFeatureCollection)
                        )

                        CircleLayer(
                            id = "user-location-layer",
                            source = userSource,
                            color = const(Color.Blue),
                            radius = const(7.dp)
                        )
                    }
                }
            }


        }


        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(horizontal = 10.dp, vertical = 30.dp)){
            LockTrackingButton(operation = { lock = !lock },5,lock, modifier = Modifier
                .fillMaxWidth())
        }
        BottomBar(camera = camera,Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .padding(bottom = 12.dp))
    }
}


