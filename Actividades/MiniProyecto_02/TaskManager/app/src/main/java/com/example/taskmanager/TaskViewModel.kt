package com.example.taskmanager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FiltroTareas { TODAS, PENDIENTES, COMPLETADAS }

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = TaskDatabase.getDatabase(application).taskDao()

    val filtroActual = MutableStateFlow(FiltroTareas.TODAS)

    val listaTareas: StateFlow<List<TaskEntity>> = combine(
        dao.obtenerTodas(),
        filtroActual
    ) { tareas, filtro ->
        when (filtro) {
            FiltroTareas.TODAS -> tareas
            FiltroTareas.PENDIENTES -> tareas.filter { !it.completada }
            FiltroTareas.COMPLETADAS -> tareas.filter { it.completada }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun agregarTarea(titulo: String, descripcion: String) {
        if (titulo.isNotBlank()) {
            viewModelScope.launch {
                dao.insertar(TaskEntity(titulo = titulo, descripcion = descripcion))
            }
        }
    }

    fun cambiarEstadoCompletada(tarea: TaskEntity, completada: Boolean) {
        viewModelScope.launch {
            dao.actualizar(tarea.copy(completada = completada))
        }
    }

    fun eliminarTarea(tarea: TaskEntity) {
        viewModelScope.launch {
            dao.eliminar(tarea)
        }
    }

    fun eliminarCompletadas() {
        viewModelScope.launch {
            dao.eliminarCompletadas()
        }
    }

    fun cambiarFiltro(filtro: FiltroTareas) {
        filtroActual.value = filtro
    }
}