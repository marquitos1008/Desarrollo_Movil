package com.example.practica14

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

// Función auxiliar para convertir el Vector Asset rojo a Bitmap (Fase 1 del Reto)
fun vectorToBitmap(context: Context, drawableId: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(context, drawableId)!!
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth.coerceAtLeast(64),
        drawable.intrinsicHeight.coerceAtLeast(64),
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

@Composable
fun MapaOSMScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getDatabase(context) }
    val marcadorDao = remember { db.marcadorDao() }

    // Flujo reactivo desde Room con todos los marcadores guardados
    val marcadoresGuardados by marcadorDao.obtenerTodos().collectAsState(initial = emptyList())

    var permissionGranted by remember { mutableStateOf(false) }

    // Estados para el diálogo al hacer Long Press en el mapa
    var puntoSeleccionado by remember { mutableStateOf<GeoPoint?>(null) }
    var tituloMarcador by remember { mutableStateOf("") }

    // Solicitar permiso de GPS
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
    }

    // Verificar el permiso al iniciar la aplicación
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            permissionGranted = true
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.OpenTopo)
                    tileProvider.clearTileCache()
                    setMultiTouchControls(true)
                    controller.setZoom(17.0)
                    // Centro inicial en Los Mochis, Sinaloa
                    controller.setCenter(GeoPoint(25.7905, -108.9875))

                    // Detector de toque prolongado (Long Press) sobre el mapa
                    val mapEventsReceiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean = false

                        override fun longPressHelper(p: GeoPoint?): Boolean {
                            p?.let {
                                puntoSeleccionado = it
                                tituloMarcador = ""
                            }
                            return true
                        }
                    }
                    overlays.add(MapEventsOverlay(mapEventsReceiver))
                }
            },
            update = { mapView ->
                // 1. Configurar capa de ubicación GPS con ícono rojo personalizado (Fase 1)
                if (permissionGranted && mapView.overlays.none { it is MyLocationNewOverlay }) {
                    val iconoRojoBitmap = vectorToBitmap(context, R.drawable.ic_ubicacion_roja)

                    val locationOverlay =
                        MyLocationNewOverlay(GpsMyLocationProvider(context), mapView).apply {
                            setPersonIcon(iconoRojoBitmap)
                            setDirectionArrow(iconoRojoBitmap, iconoRojoBitmap)
                            enableMyLocation()
                            enableFollowLocation()
                        }

                    locationOverlay.runOnFirstFix {
                        mapView.post {
                            val currentLocation = locationOverlay.myLocation
                            if (currentLocation != null) {
                                mapView.controller.setZoom(17.0)
                                mapView.controller.setCenter(currentLocation)
                            }
                        }
                    }
                    mapView.overlays.add(locationOverlay)
                }

                // 2. Sincronizar los marcadores guardados en Room sobre el mapa (Fase 2)
                mapView.overlays.removeAll { it is Marker }
                marcadoresGuardados.forEach { entidad ->
                    val marker = Marker(mapView).apply {
                        position = GeoPoint(entidad.latitud, entidad.longitud)
                        title = entidad.titulo
                        snippet = "Lat: ${"%.4f".format(entidad.latitud)}, Lon: ${"%.4f".format(entidad.longitud)}"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    mapView.overlays.add(marker)
                }

                mapView.invalidate()
            }
        )

        // Botón flotante para limpiar marcadores guardados si deseas hacer pruebas nuevas
        if (marcadoresGuardados.isNotEmpty()) {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        marcadorDao.eliminarTodos()
                        Toast.makeText(context, "Marcadores eliminados", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Borrar marcadores")
            }
        }

        // Diálogo para ingresar el título del marcador al hacer Long Press
        puntoSeleccionado?.let { geoPoint ->
            AlertDialog(
                onDismissRequest = { puntoSeleccionado = null },
                title = { Text("Guardar Punto de Interés") },
                text = {
                    Column {
                        Text(
                            text = "Coordenadas:\n${"%.5f".format(geoPoint.latitude)}, ${"%.5f".format(geoPoint.longitude)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = tituloMarcador,
                            onValueChange = { tituloMarcador = it },
                            label = { Text("Título del marcador") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val tituloFinal = tituloMarcador.ifBlank { "Punto guardado" }
                            scope.launch {
                                marcadorDao.insertar(
                                    MarcadorEntity(
                                        latitud = geoPoint.latitude,
                                        longitud = geoPoint.longitude,
                                        titulo = tituloFinal
                                    )
                                )
                            }
                            puntoSeleccionado = null
                            Toast.makeText(context, "Guardado en Room", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { puntoSeleccionado = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}