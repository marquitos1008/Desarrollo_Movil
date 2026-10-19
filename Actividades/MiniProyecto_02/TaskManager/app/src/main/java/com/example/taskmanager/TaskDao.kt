package com.example.taskmanager

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tareas ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(tarea: TaskEntity)

    @Update
    suspend fun actualizar(tarea: TaskEntity)

    @Delete
    suspend fun eliminar(tarea: TaskEntity)

    @Query("DELETE FROM tareas WHERE completada = 1")
    suspend fun eliminarCompletadas()
}