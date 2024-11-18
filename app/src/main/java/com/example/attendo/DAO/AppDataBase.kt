package com.example.attendo.DAO
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.attendo.DAO.UserDao
import com.example.attendo.DAO.EventDao
import com.example.attendo.modelos.Attendee
import com.example.attendo.modelos.Event
import com.example.attendo.modelos.User

@Database(entities = [User::class, Event::class, Attendee::class], version = 2) // Incrementamos la versión
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
    abstract fun attendeeDao(): AttendeeDao  // Añadimos el DAO de Attendee

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Obtener la instancia única de la base de datos
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"  // Nombre de la base de datos
                )
                    .fallbackToDestructiveMigration()  // Esta es una opción para eliminar datos en caso de cambios en el esquema
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
