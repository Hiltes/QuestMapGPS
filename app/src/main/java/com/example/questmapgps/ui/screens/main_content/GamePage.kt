package com.example.questmapgps.ui.screens.main_content

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
//import com.example.questmapgps.ui.components.LockTrackingButton
import com.example.questmapgps.ui.screens.BottomBar
import com.example.questmapgps.ui.sensors.LocationHelper
import com.example.questmapgps.ui.sensors.PointData
import com.example.questmapgps.ui.sensors.PointInfoDialog
import io.github.dellisd.spatialk.geojson.Feature
import io.github.dellisd.spatialk.geojson.FeatureCollection
import io.github.dellisd.spatialk.geojson.Point
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult
import java.io.File
import kotlin.time.Duration.Companion.seconds


@Composable
fun GamePage(onNavigateBack: () -> Unit) {

    Box(modifier = Modifier.fillMaxSize()) {

        var lock by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val locationHelper = remember { LocationHelper(context) }
        var location by remember { mutableStateOf<Pair<Double, Double>?>(null) }
        var showPermissionInfo by remember { mutableStateOf(false) }
        var showGpsInfo by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        var file by remember { mutableStateOf<File?>(null) }
        var loadError by remember { mutableStateOf<String?>(null) }
        var selectedPoint by remember { mutableStateOf<PointData?>(null) }

        val camera = rememberCameraState(
            firstPosition = CameraPosition(
                target = Position(22.003122228653037, 50.03120384606565),
                zoom = 13.0
            )
        )

        var hasPermission by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
        }

        var isGpsEnabled by remember {
            mutableStateOf(false)
        }

        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    val permissionGranted = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    hasPermission = permissionGranted
                    showPermissionInfo = !permissionGranted

                    if (permissionGranted) {
                        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        isGpsEnabled = gpsEnabled
                        showGpsInfo = !gpsEnabled
                    } else {
                        isGpsEnabled = false
                        showGpsInfo = false
                    }
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            observer.onStateChanged(lifecycleOwner, Lifecycle.Event.ON_RESUME)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            hasPermission = isGranted
        }

        if (showPermissionInfo) {
            AlertDialog(
                onDismissRequest = { /* Nie zamykaj */ },
                title = { Text("Dostęp do lokalizacji", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall, fontSize = 20.sp, textAlign = TextAlign.Center) },
                text = { Text("Brakuje wymaganych pozwoleń,\nbez nich nie przejdziemy dalej...", modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp), color = MaterialTheme.colorScheme.onPrimary) },
                confirmButton = {
                    TextButton(
                        onClick = { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                        modifier = Modifier.padding(6.dp).height(32.dp).defaultMinSize(minHeight = 1.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) { Text("OK, zezwól", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall) }
                },
            )
        }

        if (showGpsInfo) {
            AlertDialog(
                onDismissRequest = { /* Nie zamykaj */ },
                title = { Text("GPS jest wyłączony", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall, fontSize = 20.sp, textAlign = TextAlign.Center) },
                text = { Text("Lokalizacja w systemie jest wyłączona.\nWłącz GPS, aby kontynuować.", modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp), color = MaterialTheme.colorScheme.onPrimary) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.padding(6.dp).height(32.dp).defaultMinSize(minHeight = 1.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) { Text("Przejdź do ustawień", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall) }
                },
            )
        }

        LaunchedEffect(Unit) {
            try {
                withContext(Dispatchers.IO) {
                    val f = File(context.cacheDir, "map.geojson")
                    if (!f.exists()) {
                        context.assets.open("map.geojson").use { input -> f.outputStream().use { output -> input.copyTo(output) } }
                    }
                    file = f
                }
            } catch (e: Exception) {
                loadError = "Błąd ładowania mapy: ${e.message}"
            }
        }

        LaunchedEffect(Unit) {
            snapshotFlow { hasPermission && isGpsEnabled }
                .collect { canLocate ->
                    if (canLocate) {
                        delay(500)
                        while (isActive) {
                            try {
                                val newLocation = locationHelper.getCurrentLocation()
                                if (newLocation != null) {
                                    // Aktualizuj stan tylko jeśli nowa lokalizacja nie jest nullem
                                    location = newLocation
                                } else {
                                    // Jeśli jest null, nie rób nic i zachowaj starą pozycję.
                                    Log.w("GamePage", "Otrzymano null z LocationHelper. Zachowuję ostatnią znaną pozycję.")
                                }

                            } catch (e: Exception) {
                                Log.e("GamePage", "Błąd w pętli lokalizacyjnej", e)
                            }
                            delay(5000)
                        }
                    }
                }
        }

        LaunchedEffect(location, lock) {
            if (lock && location != null) {
                camera.animateTo(
                    finalPosition = camera.position.copy(
                        target = Position(location!!.second, location!!.first),
                        zoom = 15.0
                    ),
                    duration = 2.seconds,
                )
            }
        }

        selectedPoint?.let { point ->
            PointInfoDialog(pointData = point, onDismiss = { selectedPoint = null })
        }

        when {
            loadError != null -> { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("❌ Błąd: $loadError") } }
            file == null -> { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
            else -> {
                MaplibreMap(
                    cameraState = camera,
                    baseStyle = BaseStyle.Uri("https://api.maptiler.com/maps/basic-v2/style.json?key=3EzNiP9jPuozp4ZM6TiX")
                ) {
                    val myGeoJsonSource = rememberGeoJsonSource(data = GeoJsonData.Uri(file!!.toUri().toString()))

                    CircleLayer(
                        id = "my-geojson-points",
                        source = myGeoJsonSource,
                        color = const(Color.Red),
                        radius = const(6.dp),
                        onClick = { features ->
                            try {
                                features.firstOrNull()?.let { feature ->
                                    val jsonObject = JSONObject(feature.json())
                                    val properties = jsonObject.getJSONObject("properties")
                                    selectedPoint = PointData(
                                        name = properties.getString("name"),
                                        description = properties.getString("description"),
                                        hint = properties.getString("hint"),
                                        code = properties.getString("code")
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e("GamePage", "Błąd parsowania punktu", e)
                            }
                            ClickResult.Consume
                        }
                    )

                    location?.let { loc ->
                        val userFeatureCollection = FeatureCollection(listOf(Feature(geometry = Point(Position(longitude = loc.second, latitude = loc.first)))))
                        val userSource = rememberGeoJsonSource(data = GeoJsonData.Features(userFeatureCollection))
                        CircleLayer(id = "user-location-layer", source = userSource, color = const(Color.Blue), radius = const(7.dp))
                    }
                }
            }
        }

//        Row(
//            verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.Start,
//            modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(horizontal = 10.dp, vertical = 30.dp)
//        ) {
//            LockTrackingButton(operation = { lock = !lock }, 5, lock, modifier = Modifier.fillMaxWidth())
//        }

        BottomBar(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 12.dp),
            onLocalizeMeClick = {
                scope.launch {
                    if (location != null) {
                        camera.animateTo(
                            finalPosition = camera.position.copy(
                                target = Position(location!!.second, location!!.first),
                                zoom = 15.0
                            ),
                            duration = 2.seconds
                        )
                    } else {
                        val oneTimeLocation = locationHelper.getCurrentLocation()
                        if (oneTimeLocation != null) {
                            location = oneTimeLocation
                            camera.animateTo(
                                finalPosition = camera.position.copy(
                                    target = Position(oneTimeLocation.second, oneTimeLocation.first),
                                    zoom = 15.0
                                ),
                                duration = 2.seconds
                            )
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Nie można teraz pobrać lokalizacji", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        )
    }
}