package com.example.almacenamientodemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PantallaAlmacenamiento()
                }
            }
        }
    }
}

@Composable
fun PantallaAlmacenamiento() {
    val context = LocalContext.current

    var nombreArchivo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var esMemoriaInterna by remember { mutableStateOf(true) }

    // Launcher para permisos de almacenamiento externo
    val launcherPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val concedido = permisos[Manifest.permission.READ_EXTERNAL_STORAGE] == true ||
                permisos[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
        if (concedido) {
            Toast.makeText(context, "Permiso concedido", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    fun verificarYPedirPermisos(): Boolean {
        val read = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        val write = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return if (read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            launcherPermiso.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
            false
        }
    }

    fun guardarArchivo() {
        if (nombreArchivo.isBlank()) {
            Toast.makeText(context, "Ingresa un nombre de archivo", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val dir = if (esMemoriaInterna) context.filesDir else context.getExternalFilesDir(null)
            val file = File(dir, nombreArchivo)
            FileOutputStream(file).use { fos ->
                fos.write(contenido.toByteArray())
            }
            val destino = if (esMemoriaInterna) "Memoria Interna" else "SDCard/Externo"
            Toast.makeText(context, "Guardado en $destino", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun leerArchivo() {
        if (nombreArchivo.isBlank()) {
            Toast.makeText(context, "Ingresa un nombre de archivo", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val dir = if (esMemoriaInterna) context.filesDir else context.getExternalFilesDir(null)
            val file = File(dir, nombreArchivo)
            if (!file.exists()) {
                Toast.makeText(context, "El archivo no existe", Toast.LENGTH_SHORT).show()
                return
            }
            val textoLeido = FileInputStream(file).bufferedReader().use { it.readText() }
            contenido = textoLeido
            Toast.makeText(context, "Lectura correcta", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Gestor de Almacenamiento",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = nombreArchivo,
            onValueChange = { nombreArchivo = it },
            label = { Text("Nombre del archivo (ej. notas.txt)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = contenido,
            onValueChange = { contenido = it },
            label = { Text("Escribe el contenido aquí...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(bottom = 16.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            RadioButton(
                selected = esMemoriaInterna,
                onClick = { esMemoriaInterna = true }
            )
            Text(text = "Interno", modifier = Modifier.padding(end = 16.dp))

            RadioButton(
                selected = !esMemoriaInterna,
                onClick = { esMemoriaInterna = false }
            )
            Text(text = "SDCard / Externo")
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    if (esMemoriaInterna || verificarYPedirPermisos()) {
                        guardarArchivo()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            ) {
                Text("Guardar")
            }

            Button(
                onClick = {
                    if (esMemoriaInterna || verificarYPedirPermisos()) {
                        leerArchivo()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            ) {
                Text("Leer")
            }
        }
    }
}