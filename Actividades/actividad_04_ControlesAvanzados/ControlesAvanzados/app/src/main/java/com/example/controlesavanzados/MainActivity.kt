package com.example.controlesavanzados

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencias a los componentes de UI
        val spCategoria = findViewById<Spinner>(R.id.spCategoria)
        val rgTurno = findViewById<RadioGroup>(R.id.rgTurno)
        val cbMovil = findViewById<CheckBox>(R.id.cbMovil)
        val cbWeb = findViewById<CheckBox>(R.id.cbWeb)
        val cbBD = findViewById<CheckBox>(R.id.cbBD)
        val swNotificaciones = findViewById<Switch>(R.id.swNotificaciones)
        val sbNivel = findViewById<SeekBar>(R.id.sbNivel)
        val tvNivelValor = findViewById<TextView>(R.id.tvNivelValor)
        val btnProcesar = findViewById<Button>(R.id.btnProcesar)
        val tvResumen = findViewById<TextView>(R.id.tvResumen)

        // Configuración del Spinner mediante ArrayAdapter
        val opciones = arrayOf("Estudiante", "Docente", "Desarrollador", "Invitado")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opciones)
        spCategoria.adapter = adapter

        // Escuchador en tiempo real para el SeekBar
        sbNivel.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvNivelValor.text = "Nivel de experiencia (SeekBar): $progress%"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Botón de Procesamiento de la información
        btnProcesar.setOnClickListener {
            val categoria = spCategoria.selectedItem.toString()

            val turnoId = rgTurno.checkedRadioButtonId
            val turno = if (turnoId != -1) findViewById<RadioButton>(turnoId).text.toString() else "Sin seleccionar"

            val notif = if (swNotificaciones.isChecked) "Activadas" else "Desactivadas"
            val nivel = sbNivel.progress

            val intereses = mutableListOf<String>()
            if (cbMovil.isChecked) intereses.add("Móvil")
            if (cbWeb.isChecked) intereses.add("Web")
            if (cbBD.isChecked) intereses.add("Bases de Datos")
            val interesesTexto = if (intereses.isNotEmpty()) intereses.joinToString(", ") else "Ninguno"

            // Presentación del resumen en pantalla
            tvResumen.text = """
                📌 RESUMEN DE REGISTRO
                • Categoría: $categoria
                • Turno: $turno
                • Intereses: $interesesTexto
                • Notificaciones: $notif
                • Nivel de experiencia: $nivel%
            """.trimIndent()
        }
    }
}