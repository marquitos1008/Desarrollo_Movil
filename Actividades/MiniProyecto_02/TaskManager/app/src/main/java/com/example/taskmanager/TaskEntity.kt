package com.example.taskmanager

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tareas")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val completada: Boolean = false
)