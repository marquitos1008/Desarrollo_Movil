package com.example.sharedpreferencesdemo

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var etUsuario: EditText
    private lateinit var etCorreo: EditText
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etUsuario = findViewById(R.id.etUsuario)
        etCorreo = findViewById(R.id.etCorreo)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        val btnCargar = findViewById<Button>(R.id.btnCargar)
        val btnBorrar = findViewById<Button>(R.id.btnBorrar)

        // Inicialización del archivo de preferencias
        sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)

        // Cargar automáticamente los datos al abrir la app
        cargarDatos()

        btnGuardar.setOnClickListener { guardarDatos() }

        btnCargar.setOnClickListener {
            cargarDatos()
            Toast.makeText(this, "Datos cargados desde SharedPreferences", Toast.LENGTH_SHORT).show()
        }

        btnBorrar.setOnClickListener { borrarDatos() }
    }

    private fun guardarDatos() {
        val usuario = etUsuario.text.toString().trim()
        val correo = etCorreo.text.toString().trim()

        if (usuario.isNotEmpty() && correo.isNotEmpty()) {
            val editor = sharedPreferences.edit()
            editor.putString("KEY_USUARIO", usuario)
            editor.putString("KEY_CORREO", correo)
            editor.apply() // Se guardan de manera asíncrona

            Toast.makeText(this, "Datos guardados con éxito", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Ingresa ambos campos antes de guardar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarDatos() {
        val usuarioGuardado = sharedPreferences.getString("KEY_USUARIO", "")
        val correoGuardado = sharedPreferences.getString("KEY_CORREO", "")

        etUsuario.setText(usuarioGuardado)
        etCorreo.setText(correoGuardado)
    }

    private fun borrarDatos() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        etUsuario.text.clear()
        etCorreo.text.clear()
        Toast.makeText(this, "Datos borrados de SharedPreferences", Toast.LENGTH_SHORT).show()
    }
}