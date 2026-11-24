package com.example.miniproyecto03

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import java.io.File

class MainActivity : ComponentActivity() {

    private val viewModel: PublicacionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PantallaRedSocial(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRedSocial(viewModel: PublicacionViewModel) {
    val context = LocalContext.current
    val publicaciones by viewModel.publicaciones.collectAsState()

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var uriImagenSeleccionada by remember { mutableStateOf<Uri?>(null) }
    var uriFotoCamaraTemporal by remember { mutableStateOf<Uri?>(null) }

    // Launcher para seleccionar imagen de la Galería
    val launcherGalería = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { uriImagenSeleccionada = it }
    }

    // Launcher para tomar fotografía con la Cámara
    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito: Boolean ->
        if (exito && uriFotoCamaraTemporal != null) {
            uriImagenSeleccionada = uriFotoCamaraTemporal
        }
    }

    fun crearArchivoTemporalFoto(): Uri {
        val directorio = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val archivo = File.createTempFile("FOTO_${System.currentTimeMillis()}_", ".jpg", directorio)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            archivo
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Red Social Local", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título de la Publicación") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { launcherGalería.launch("image/*") },
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                ) {
                    Text("Galería")
                }

                Button(
                    onClick = {
                        val uri = crearArchivoTemporalFoto()
                        uriFotoCamaraTemporal = uri
                        launcherCamara.launch(uri)
                    },
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                ) {
                    Text("Cámara")
                }
            }

            uriImagenSeleccionada?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "Vista Previa",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .padding(bottom = 12.dp)
                )
            }

            Button(
                onClick = {
                    if (titulo.isNotBlank() && uriImagenSeleccionada != null) {
                        viewModel.guardarPublicacion(
                            titulo = titulo,
                            descripcion = descripcion,
                            fotoUri = uriImagenSeleccionada.toString()
                        )
                        titulo = ""
                        descripcion = ""
                        uriImagenSeleccionada = null
                        Toast.makeText(context, "Publicado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Ingresa un título y selecciona una imagen", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Publicar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Feed de Publicaciones (${publicaciones.size}):",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(publicaciones, key = { it.id }) { pub ->
                    ItemPublicacion(
                        publicacion = pub,
                        onEliminar = { viewModel.eliminarPublicacion(pub) }
                    )
                }
            }
        }
    }
}

@Composable
fun ItemPublicacion(publicacion: PublicacionEntity, onEliminar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = publicacion.fotoUri,
                contentDescription = publicacion.titulo,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = publicacion.titulo, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    if (publicacion.descripcion.isNotBlank()) {
                        Text(text = publicacion.descripcion, fontSize = 14.sp)
                    }
                    Text(
                        text = "Fecha: ${publicacion.fecha}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                IconButton(onClick = onEliminar) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}