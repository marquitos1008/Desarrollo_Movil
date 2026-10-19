package com.example.taskmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PantallaAdministradorTareas(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAdministradorTareas(viewModel: TaskViewModel) {
    val listaTareas by viewModel.listaTareas.collectAsState()
    val filtroActual by viewModel.filtroActual.collectAsState()

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var menuMenuDesplegable by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administrador de Tareas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { menuMenuDesplegable = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menú")
                    }
                    DropdownMenu(
                        expanded = menuMenuDesplegable,
                        onDismissRequest = { menuMenuDesplegable = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Mostrar Todas") },
                            onClick = {
                                viewModel.cambiarFiltro(FiltroTareas.TODAS)
                                menuMenuDesplegable = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mostrar Pendientes") },
                            onClick = {
                                viewModel.cambiarFiltro(FiltroTareas.PENDIENTES)
                                menuMenuDesplegable = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mostrar Completadas") },
                            onClick = {
                                viewModel.cambiarFiltro(FiltroTareas.COMPLETADAS)
                                menuMenuDesplegable = false
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Limpiar Completadas", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                viewModel.eliminarCompletadas()
                                menuMenuDesplegable = false
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Formulario para Agregar Tarea
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título de la Tarea") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (Opcional)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )

            Button(
                onClick = {
                    viewModel.agregarTarea(titulo, descripcion)
                    titulo = ""
                    descripcion = ""
                },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Agregar Tarea")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Filtro activo: ${filtroActual.name} (${listaTareas.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Listado Dinámico con LazyColumn
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(listaTareas, key = { it.id }) { tarea ->
                    ItemTarea(
                        tarea = tarea,
                        onEstadoCambiado = { completada ->
                            viewModel.cambiarEstadoCompletada(tarea, completada)
                        },
                        onEliminar = { viewModel.eliminarTarea(tarea) }
                    )
                }
            }
        }
    }
}

@Composable
fun ItemTarea(
    tarea: TaskEntity,
    onEstadoCambiado: (Boolean) -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (tarea.completada)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarea.completada,
                onCheckedChange = onEstadoCambiado
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 8.dp)
            ) {
                Text(
                    text = tarea.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textDecoration = if (tarea.completada) TextDecoration.LineThrough else TextDecoration.None
                )
                if (tarea.descripcion.isNotBlank()) {
                    Text(
                        text = tarea.descripcion,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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