package com.example.recyclerviewdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EstudianteAdapter(
    private val listaEstudiantes: List<Estudiante>,
    private val onItemClick: (Estudiante) -> Unit
) : RecyclerView.Adapter<EstudianteAdapter.EstudianteViewHolder>() {

    class EstudianteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvMatricula: TextView = itemView.findViewById(R.id.tvMatricula)
        val tvCarrera: TextView = itemView.findViewById(R.id.tvCarrera)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstudianteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_estudiante, parent, false)
        return EstudianteViewHolder(view)
    }

    override fun onBindViewHolder(holder: EstudianteViewHolder, position: Int) {
        val estudiante = listaEstudiantes[position]
        holder.tvNombre.text = estudiante.nombre
        holder.tvMatricula.text = "Matrícula: ${estudiante.matricula}"
        holder.tvCarrera.text = estudiante.carrera

        // Captura del evento de clic sobre el elemento
        holder.itemView.setOnClickListener {
            onItemClick(estudiante)
        }
    }

    override fun getItemCount(): Int = listaEstudiantes.size
}