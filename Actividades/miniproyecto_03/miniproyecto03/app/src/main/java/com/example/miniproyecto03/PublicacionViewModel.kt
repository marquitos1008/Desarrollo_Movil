package com.example.miniproyecto03

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PublicacionViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).publicacionDao()

    val publicaciones: StateFlow<List<PublicacionEntity>> = dao.obtenerTodas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun guardarPublicacion(titulo: String, descripcion: String, fotoUri: String) {
        if (titulo.isNotBlank() && fotoUri.isNotBlank()) {
            val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val fechaActual = formatoFecha.format(Date())

            viewModelScope.launch {
                dao.insertar(
                    PublicacionEntity(
                        titulo = titulo,
                        descripcion = descripcion,
                        fotoUri = fotoUri,
                        fecha = fechaActual
                    )
                )
            }
        }
    }

    fun eliminarPublicacion(publicacion: PublicacionEntity) {
        viewModelScope.launch {
            dao.eliminar(publicacion)
        }
    }
}