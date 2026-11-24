package com.example.practica14

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MarcadorDao {
    @Query("SELECT * FROM marcadores")
    fun obtenerTodos(): Flow<List<MarcadorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(marcador: MarcadorEntity)

    @Query("DELETE FROM marcadores")
    suspend fun eliminarTodos()
}