package com.example.questmapgps.ui.screens.main_content

//import com.example.questmapgps.ui.components.LockTrackingButton
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.questmapgps.R
import com.example.questmapgps.ui.screens.BottomBar
import com.example.questmapgps.ui.sensors.LocationHelper
import com.example.questmapgps.ui.sensors.PointData
import com.example.questmapgps.ui.sensors.PointInfoDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.Feature.Companion.getStringProperty
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Geometry
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position
import java.io.File
import java.util.Locale
import java.util.Locale.getDefault
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.seconds

fun distanceMeters(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Double {
    val R = 6371e3
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) *
            cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2.0)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}

@Composable
fun GamePage(
    gameViewModel: GameViewModel,
    onNavigateBack: () -> Unit
) {

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

        var selectedFeature by remember {mutableStateOf<Feature<Geometry, JsonObject?>?>(null)}


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

        val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
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
                        modifier = Modifier
                            .padding(6.dp)
                            .height(32.dp)
                            .defaultMinSize(minHeight = 1.dp),
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
                        modifier = Modifier
                            .padding(6.dp)
                            .height(32.dp)
                            .defaultMinSize(minHeight = 1.dp),
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

        when {
            loadError != null -> { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("❌ Błąd: $loadError") } }
            file == null -> { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
            else -> {
                val point = painterResource(R.drawable.point)
                val pointVisited = painterResource(R.drawable.point_visited)
                MaplibreMap(
                    cameraState = camera,
                    baseStyle = BaseStyle.Uri("https://api.maptiler.com/maps/basic-v2/style.json?key=3EzNiP9jPuozp4ZM6TiX")

                ) {

                    val myGeoJsonSource = rememberGeoJsonSource(data = GeoJsonData.Uri(file!!.toUri().toString()))

                    // pobieranie punktów z których user wpisał poprawnie kod
//                    val userData by gameViewModel.userData.collectAsState()
//                    val solvedcodePoints = userData?.codesSolvedPoints ?: emptyList()


                    SymbolLayer(
                        id = "my-geojson-points",
                        source = myGeoJsonSource,
                        iconImage = image(point),

                        onClick = { features ->
                    selectedFeature = features.firstOrNull()
                    ClickResult.Consume
                            }
                    )
                    selectedFeature?.let { feature ->
                        val pos = (feature.geometry as Point).coordinates
                        val point = PointData(
                            name = feature.getStringProperty("name").toString(),
                            description = feature.getStringProperty("description").toString(),
                            hint = feature.getStringProperty("hint").toString(),
                            code = feature.getStringProperty("code").toString(),
                            latitude =  pos.latitude,
                            longitude = pos.longitude
                        )
                        PointInfoDialog(
                            pointData = point,
                            onDismiss = { selectedFeature = null },
                            onCodeCorrect = {
                                gameViewModel.markCodeAsSolved(pointName = point.name )
                            }
                        )
                    }

                    location?.let { loc ->
                        val userPoint = Point(Position(longitude = loc.second, latitude = loc.first))

                        val userFeature = Feature<Point, Map<String, Any>>(
                            geometry = userPoint,
                            properties = JsonObject(emptyMap())
                        )

                        val userFeatureCollection = FeatureCollection(userFeature)

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
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 12.dp),
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
                                Toast.makeText(context, "Pobieranie lokalizacji...", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        )
    }
}