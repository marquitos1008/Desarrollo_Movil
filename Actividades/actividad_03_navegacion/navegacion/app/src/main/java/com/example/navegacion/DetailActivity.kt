package com.example.navegacion

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val tvNombreRecibido = findViewById<TextView>(R.id.tvNombreRecibido)
        val tvCorreoRecibido = findViewById<TextView>(R.id.tvCorreoRecibido)
        val btnRegresar = findViewById<Button>(R.id.btnRegresar)

        // Obtener los datos enviados a través del Intent
        val nombre = intent.getStringExtra("EXTRA_NOMBRE") ?: "Sin nombre"
        val correo = intent.getStringExtra("EXTRA_CORREO") ?: "Sin correo"

        // Mostrar la información en los TextViews
        tvNombreRecibido.text = "Nombre recibido: $nombre"
        tvCorreoRecibido.text = "Correo recibido: $correo"

        // Botón para finalizar esta Activity y regresar a la pantalla anterior
        btnRegresar.setOnClickListener {
            finish()
        }
    }
}