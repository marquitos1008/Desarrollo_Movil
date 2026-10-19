package com.example.roomdemo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EstudianteViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).estudianteDao()

    val listaEstudiantes: StateFlow<List<EstudianteEntity>> = dao.obtenerEstudiantes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun agregarEstudiante(nombre: String, matricula: String, carrera: String) {
        if (nombre.isNotBlank() && matricula.isNotBlank()) {
            viewModelScope.launch {
                dao.insertarEstudiante(
                    EstudianteEntity(nombre = nombre, matricula = matricula, carrera = carrera)
                )
            }
        }
    }

    fun eliminarEstudiante(estudiante: EstudianteEntity) {
        viewModelScope.launch {
            dao.eliminarEstudiante(estudiante)
        }
    }
}