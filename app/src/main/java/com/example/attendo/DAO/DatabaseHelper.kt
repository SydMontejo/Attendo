package com.example.attendo.DAO

import com.example.attendo.modelos.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


fun getUsersFromDatabase(userDao: UserDao, onResult: (List<User>) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        val users = userDao.getAllUsers()
        onResult(users)
    }
}

fun saveUserToDatabase(userDao: UserDao, user: User) {
    CoroutineScope(Dispatchers.IO).launch {
        userDao.insertUser(user)
    }
}
