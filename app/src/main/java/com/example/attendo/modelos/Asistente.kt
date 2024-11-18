package com.example.attendo.modelos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendees")

data class Attendee(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cod: String,
    val name: String,
    val email: String,
    val eventId: Int,  // Relación con el evento
    val attendance: Boolean = false  // Estado de asistencia (true si asistió)
)