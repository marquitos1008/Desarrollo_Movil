package com.example.recyclerviewdemo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewEstudiantes)

        // 1. Colección de datos dinámicos de prueba
        val listaEstudiantes = listOf(
            Estudiante("Marcos Gálvez", "2026001", "Ing. en Software"),
            Estudiante("Ana Martínez", "2026002", "Ing. en Computación"),
            Estudiante("Carlos López", "2026003", "Lic. en Redes"),
            Estudiante("Diana Ramos", "2026004", "Ing. Mecatrónica"),
            Estudiante("Eduardo Silva", "2026005", "Ing. en Software"),
            Estudiante("Fernanda Gómez", "2026006", "Lic. en Redes"),
            Estudiante("Gabriel Cruz", "2026007", "Ing. en Computación"),
            Estudiante("Hugo Hernández", "2026008", "Ing. Mecatrónica")
        )

        // 2. Configurar el LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 3. Asignar el Adapter con lambda para el evento clic
        recyclerView.adapter = EstudianteAdapter(listaEstudiantes) { estudiante ->
            Toast.makeText(this, "Seleccionado: ${estudiante.nombre}", Toast.LENGTH_SHORT).show()
        }
    }
}