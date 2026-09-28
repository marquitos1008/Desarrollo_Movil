package com.example.registroestudiantescompose

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

// -------------------------------------------------------------
// 1. CONFIGURACIÓN DE NAVEGACIÓN ENTRE COMPOSABLES (NavHost)
// -------------------------------------------------------------
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "registro") {
        composable("registro") {
            RegistroScreen(navController = navController)
        }
        composable(
            route = "detalle/{matricula}/{nombre}/{carrera}/{turno}/{estatus}",
            arguments = listOf(
                navArgument("matricula") { type = NavType.StringType },
                navArgument("nombre") { type = NavType.StringType },
                navArgument("carrera") { type = NavType.StringType },
                navArgument("turno") { type = NavType.StringType },
                navArgument("estatus") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            DetalleScreen(
                navController = navController,
                matricula = backStackEntry.arguments?.getString("matricula") ?: "",
                nombre = backStackEntry.arguments?.getString("nombre") ?: "",
                carrera = backStackEntry.arguments?.getString("carrera") ?: "",
                turno = backStackEntry.arguments?.getString("turno") ?: "",
                estatus = backStackEntry.arguments?.getString("estatus") ?: ""
            )
        }
    }
}

// -------------------------------------------------------------
// 2. PANTALLA 1: FORMULARIO DE REGISTRO (Jetpack Compose)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("RegistroPref", Context.MODE_PRIVATE) }

    // Cargar la última matrícula guardada mediante SharedPreferences
    val ultimaMatricula = remember { sharedPref.getString("ULTIMA_MATRICULA", "") ?: "" }

    var matricula by remember { mutableStateOf(ultimaMatricula) }
    var nombre by remember { mutableStateOf("") }

    // Estado del Menú Desplegable (ExposedDropdownMenu)
    val carreras = listOf("Ing. en Software", "Ing. en Computación", "Lic. en Redes", "Ing. Mecatrónica")
    var expandedDropdown by remember { mutableStateOf(false) }
    var carreraSeleccionada by remember { mutableStateOf(carreras[0]) }

    // Estado del RadioButton (Turno)
    var turnoSeleccionado by remember { mutableStateOf("Matutino") }

    // Estado del Switch (Estatus)
    var estatusActivo by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Registro de Estudiantes",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // CampoS: Matrícula y Nombre
        OutlinedTextField(
            value = matricula,
            onValueChange = { matricula = it },
            label = { Text("Matrícula") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre Completo") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // Dropdown Menu: Carrera
        Text(text = "Carrera:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
        ExposedDropdownMenuBox(
            expanded = expandedDropdown,
            onExpandedChange = { expandedDropdown = !expandedDropdown },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = carreraSeleccionada,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedDropdown,
                onDismissRequest = { expandedDropdown = false }
            ) {
                carreras.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            carreraSeleccionada = item
                            expandedDropdown = false
                        }
                    )
                }
            }
        }

        // RadioButton: Turno
        Text(text = "Turno:", fontWeight = FontWeight.SemiBold)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            RadioButton(
                selected = (turnoSeleccionado == "Matutino"),
                onClick = { turnoSeleccionado = "Matutino" }
            )
            Text(text = "Matutino", modifier = Modifier.padding(end = 16.dp))

            RadioButton(
                selected = (turnoSeleccionado == "Vespertino"),
                onClick = { turnoSeleccionado = "Vespertino" }
            )
            Text(text = "Vespertino")
        }

        // Switch: Estatus
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            Text(text = "Estatus (Activo/Inactivo):", fontWeight = FontWeight.SemiBold)
            Switch(
                checked = estatusActivo,
                onCheckedChange = { estatusActivo = it }
            )
        }

        // Botón de Registrar y Navegar
        Button(
            onClick = {
                if (matricula.isNotBlank() && nombre.isNotBlank()) {
                    // 1. Guardar la matrícula en SharedPreferences
                    sharedPref.edit().putString("ULTIMA_MATRICULA", matricula).apply()

                    // 2. Codificar strings para la ruta de navegación
                    val estatusTexto = if (estatusActivo) "Activo" else "Inactivo"
                    val matEnc = Uri.encode(matricula)
                    val nomEnc = Uri.encode(nombre)
                    val carEnc = Uri.encode(carreraSeleccionada)
                    val turEnc = Uri.encode(turnoSeleccionado)
                    val estEnc = Uri.encode(estatusTexto)

                    // 3. Navegar a la pantalla de detalle pasando argumentos
                    navController.navigate("detalle/$matEnc/$nomEnc/$carEnc/$turEnc/$estEnc")
                } else {
                    Toast.makeText(context, "Ingresa matrícula y nombre", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "Registrar Estudiante", fontSize = 16.sp)
        }
    }
}

// -------------------------------------------------------------
// 3. PANTALLA 2: PANTALLA DE CONFIRMACIÓN / DETALLE
// -------------------------------------------------------------
@Composable
fun DetalleScreen(
    navController: NavController,
    matricula: String,
    nombre: String,
    carrera: String,
    turno: String,
    estatus: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Confirmación de Registro", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "• Matrícula: $matricula", fontSize = 16.sp)
                Text(text = "• Nombre: $nombre", fontSize = 16.sp)
                Text(text = "• Carrera: $carrera", fontSize = 16.sp)
                Text(text = "• Turno: $turno", fontSize = 16.sp)
                Text(text = "• Estatus: $estatus", fontSize = 16.sp)
            }
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "Regresar al Formulario", fontSize = 16.sp)
        }
    }
}