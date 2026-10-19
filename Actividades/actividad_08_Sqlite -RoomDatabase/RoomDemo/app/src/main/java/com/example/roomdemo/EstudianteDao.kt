package com.example.roomdemo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EstudianteDao {

    @Query("SELECT * FROM estudiantes ORDER BY id DESC")
    fun obtenerEstudiantes(): Flow<List<EstudianteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEstudiante(estudiante: EstudianteEntity)

    @Delete
    suspend fun eliminarEstudiante(estudiante: EstudianteEntity)
}