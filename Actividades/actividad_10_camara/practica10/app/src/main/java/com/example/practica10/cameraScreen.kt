package com.example.practica10

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CamaraScreen() {
    val context = LocalContext.current

    // Lista observable de imágenes (Bitmaps)
    var listaImagenes by remember { mutableStateOf(listOf<Bitmap>()) }

    // Lanzador para abrir la cámara y recibir el Bitmap capturado
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            listaImagenes = listaImagenes + bitmap
        }
    }

    // Reto Entregable: Lanzador para solicitar permiso explícito de cámara
    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { esConcedido: Boolean ->
        if (esConcedido) {
            // Si el usuario concede el permiso en el diálogo, abrimos la cámara
            camaraLauncher.launch(null)
        } else {
            Toast.makeText(
                context,
                "Permiso de cámara denegado. No se puede tomar la foto.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cámara y Galería") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Verificamos si ya tenemos el permiso de cámara
                    val tienePermiso = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (tienePermiso) {
                        camaraLauncher.launch(null)
                    } else {
                        // Pedimos el permiso explícitamente en pantalla
                        permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tomar Foto")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            if (listaImagenes.isEmpty()) {
                Text("No hay fotos en la galería. ¡Toma una!")
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(listaImagenes) { bitmap ->
                        ImagenCapturadaItem(bitmap = bitmap)
                    }
                }
            }
        }
    }
}

@Composable
fun ImagenCapturadaItem(bitmap: Bitmap) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        // Convertimos el Bitmap a ImageBitmap de Compose
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Foto capturada",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}