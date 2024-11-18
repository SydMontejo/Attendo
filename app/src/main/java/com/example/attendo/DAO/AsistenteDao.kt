package com.example.attendo.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.attendo.modelos.Attendee

@Dao
interface AttendeeDao {

    @Insert
    suspend fun insertAttendee(attendee: Attendee)

    @Update
    suspend fun updateAttendee(attendee: Attendee)

    @Query("SELECT * FROM attendees WHERE eventId = :eventId AND cod = :cod LIMIT 1")
    suspend fun getAttendeeByCodAndEventId(cod: String, eventId: Int): Attendee?

    @Query("SELECT * FROM attendees WHERE eventId = :eventId")
    suspend fun getAttendeesForEvent(eventId: Int): List<Attendee>

    @Query("SELECT * FROM attendees WHERE cod = :cod LIMIT 1")
    suspend fun getAttendeeByCod(cod: String): Attendee?

    @Query("DELETE FROM attendees WHERE eventId = :eventId")
    suspend fun deleteAttendeesByEvent(eventId: Int)

    @Query("SELECT * FROM attendees WHERE cod = :cod AND eventId = :eventId LIMIT 1")
    suspend fun getAttendeeByCodAndEvent(cod: String, eventId: Int): Attendee?

    // Método para contar los asistentes de un evento específico
    @Query("SELECT COUNT(*) FROM attendees WHERE eventId = :eventId")
    suspend fun getAttendeeCountForEvent(eventId: Int): Int
}
