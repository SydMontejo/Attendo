package com.example.attendo.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.Visibility
import com.example.attendo.DAO.UserDao
import com.example.attendo.modelos.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    userDao: UserDao,
    onLoginSuccess: (Int) -> Unit,
    onBack: () -> Unit,
    //onForgotPassword: () -> Unit // Agregado el parámetro aquí
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("Usuario no registrado.") }
    var loginSuccessMessage by remember { mutableStateOf("Has iniciado sesión correctamente.") }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    //val snackbarHostState = remember { SnackbarHostState() }
    //var showPasswordResetScreen by remember { mutableStateOf(false) }
    //var email by remember { mutableStateOf("") }


    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))
                )
            )
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título estilizado
        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.h5.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = MaterialTheme.colors.primary
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Campo de texto para el nombre de usuario con ícono
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Usuario") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto para la contraseña con ícono de ojo
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Contraseña") },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White
            )
        )

        // Mensajes de error y éxito
        //if (errorMessage.isNotEmpty()) {
        //    Text(errorMessage, color = MaterialTheme.colors.error, modifier = Modifier.padding(top = 8.dp))
        //}
        //if (loginSuccessMessage.isNotEmpty()) {
         //   Text(loginSuccessMessage, color = Color(0xFF4CAF50), modifier = Modifier.padding(top = 8.dp))
        //}
        Spacer(modifier = Modifier.height(24.dp))

        // Botón de inicio de sesión
        Button(
            onClick = {
                coroutineScope.launch {
                    val user = checkUserInDatabase(userDao, username, password)
                    if (user != null) {
                        dialogMessage = loginSuccessMessage
                        showDialog = true
                        kotlinx.coroutines.delay(1000)
                        //loginSuccessMessage = "Has iniciado sesión correctamente."
                        kotlinx.coroutines.delay(1000)
                        onLoginSuccess(user.id)
                    } else {
                        dialogMessage = errorMessage
                        showDialog = true
                    //errorMessage = "Usuario no registrado."
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colors.primary
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Iniciar Sesión", style = MaterialTheme.typography.body1, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de volver
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            border = BorderStroke(1.dp, MaterialTheme.colors.primary),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Volver", style = MaterialTheme.typography.body1, color = MaterialTheme.colors.primary)
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Aviso") },
                text = { Text(dialogMessage) },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}


// Función suspendida para verificar en la base de datos
private suspend fun checkUserInDatabase(
    userDao: UserDao,
    username: String,
    password: String
): User? {
    return userDao.getUserByUsernameAndPassword(username, password)
}

private suspend fun getUserByUsername(userDao: UserDao, username: String): User? {
    return userDao.getUserByUsername(username)
}

