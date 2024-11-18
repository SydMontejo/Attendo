package com.example.attendo.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.attendo.DAO.UserDao
import com.example.attendo.modelos.User

@Composable
fun UserTable(userDao: UserDao, onBack: () -> Unit) {

    val users = remember { mutableStateOf(listOf<User>()) }

    // Obtener los usuarios en una corrutina
    LaunchedEffect(Unit) {
        // Obtenemos todos los usuarios de la base de datos
        users.value = userDao.getAllUsers()
    }

    // Estructura principal con Column para que el botón quede abajo
    Column(
        modifier = Modifier
            .fillMaxSize()  // Ocupa todo el espacio disponible
            .padding(16.dp)
    ) {
        // Mostrar la tabla de usuarios
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)  // Esto hace que ocupe el espacio restante
        ) {
            // Iteramos sobre la lista de usuarios
            items(users.value) { user ->
                // Creamos una fila para cada usuario
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Nombre: ${user.name}", modifier = Modifier.weight(2f)) // Muestra el nombre del usuario
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Password: ${user.password}", modifier = Modifier.weight(2f)) // Muestra la contraseña
                }
            }
        }

        // Aquí es donde está el botón Volver
        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre la tabla y el botón
        Button(
            modifier = Modifier.fillMaxWidth(), // Hacer que el botón ocupe el ancho completo
            onClick = onBack
        ) {
            Text("Volver")
        }
    }
}

