package com.example.attendo.DAO
import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.room.*
import com.example.attendo.modelos.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    // Obtener usuario por nombre de usuario y contraseña
    @Query("SELECT * FROM users WHERE name = :username AND password = :password LIMIT 1")
    suspend fun getUserByUsernameAndPassword(username: String, password: String): User?

    // Obtener usuario por ID
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): User?

    // Obtener usuario por nombre de usuario (solo por el nombre)
    @Query("SELECT * FROM users WHERE name = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    // Obtener usuario por correo electrónico
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    // Obtener todos los usuarios
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Update
    suspend fun updateUser(user: User)


}

