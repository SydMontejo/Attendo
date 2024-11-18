package com.example.attendo.di

import com.example.attendo.DAO.UserDao
import com.example.attendo.modelos.User

class UserRepository(private val userDao: UserDao) {

    // Función para obtener el usuario por su correo
    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email) // Esto asume que tienes una consulta para obtener un usuario por correo
    }

    // Función para actualizar el usuario
    suspend fun updateUser(user: User) {
        userDao.updateUser(user) // Llamamos al método del DAO para actualizar los datos del usuario
    }
}
