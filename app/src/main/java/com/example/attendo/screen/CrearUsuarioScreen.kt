package com.example.attendo.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.attendo.modelos.User


@Composable
fun CrearUsuarioScreen(onSave: (User) -> Unit){

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showModal by remember { mutableStateOf(false) }
    var showConfirmacion by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Crear Usuario", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { newText -> nombre = newText },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para guardar usuario
        Button(onClick = {
            if (nombre.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                onSave(User(name = nombre, email = email, password = password))
                showModal = true
            } else {
                // Puedes agregar lógica para mostrar un mensaje de error si los campos están vacíos
            }
        }) {
            Text("Guardar Usuario")
        }

        // Mostrar modal de confirmación si el usuario fue creado correctamente
        if (showModal) {
            ConfirmacionModal(
                show = showConfirmacion,
                onConfirm = {
                    val usuario = User(name = nombre, email = email, password = password)
                    onSave(usuario)
                },
                onDismiss = { showConfirmacion = false}
            )
        }
    }
}
@Composable
fun ConfirmacionModal(
    show: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    message: String = "¿Estás seguro de que quieres continuar?"
) {
    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = onDismiss) {
                            Text("Cancelar")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(onClick = {
                            onConfirm()
                            onDismiss() // Cierra el modal después de confirmar
                        }) {
                            Text("Confirmar")
                        }
                    }
                }
            }
        }
    }
}