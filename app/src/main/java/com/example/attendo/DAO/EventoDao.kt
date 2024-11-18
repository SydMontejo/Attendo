package com.example.attendo.DAO

import androidx.room.*
import com.example.attendo.modelos.Event

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    @Query("SELECT * FROM events WHERE userId = :userId")
    suspend fun getEventsByUserId(userId: Int): List<Event>

    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventById(eventId: Int): Event

    @Query("SELECT name FROM events WHERE id = :eventId LIMIT 1")
    suspend fun getEventNameById(eventId: Int): String

    // Método para eliminar un evento por su ID
    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteEventById(eventId: Int)


}
