package com.example.toolbarmenusdemo

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Configurar la MaterialToolbar como ActionBar principal
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 2. Configurar evento de clic para desplegar el PopupMenu Contextual
        val btnPopupMenu = findViewById<Button>(R.id.btnPopupMenu)
        btnPopupMenu.setOnClickListener { view ->
            mostrarPopupMenu(view)
        }
    }

    // 3. Inflar el menú de opciones (OptionsMenu) en la Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // 4. Gestionar los eventos de selección del OptionsMenu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_buscar -> {
                Toast.makeText(this, "Acción: Buscar", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_compartir -> {
                Toast.makeText(this, "Acción: Compartir", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_ajustes -> {
                Toast.makeText(this, "Acción: Ajustes", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_acerca -> {
                Toast.makeText(this, "Práctica 7 - Material Design 3", Toast.LENGTH_LONG).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // 5. Crear y desplegar el PopupMenu
    private fun mostrarPopupMenu(view: View) {
        val popup = PopupMenu(this, view)

        // Inflar pasando el archivo XML y la propiedad .menu
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)

        // Configurar la escucha de clics en los ítems
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.popup_opcion1 -> {
                    Toast.makeText(this, "Opción 1 seleccionada", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.popup_opcion2 -> {
                    Toast.makeText(this, "Opción 2 seleccionada", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }

        popup.show()
    }
}