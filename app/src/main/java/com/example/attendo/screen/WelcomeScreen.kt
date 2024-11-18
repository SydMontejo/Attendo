package com.example.attendo.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendo.R

@Composable
fun WelcomeScreen(onLoginClick: () -> Unit,
                  onRegisterClick: () -> Unit,
                  onShowUsersClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            //.padding(32.dp) // Más espacio en los márgenes
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFBBDEFB), Color(0xFF90CAF9))
                )
            )
        // Fondo del tema
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        // Logo de la aplicación
        Image(
            painter = painterResource(id = R.drawable.attendologo), // Reemplaza 'attendo_logo' con el nombre de tu archivo de logo
            contentDescription = "Logo de Attendo",
            modifier = Modifier
                .size(500.dp) // Tamaño del logo
                .padding(bottom = 16.dp)
        )

        //Spacer(modifier = Modifier.height(48.dp)) // Más espacio entre título y botones

        // Botón "Iniciar Sesión"
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp), // Aumentamos la altura del botón
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White // Texto blanco
            ),
            shape = MaterialTheme.shapes.medium // Borde redondeado
        ) {
            Text("Iniciar Sesión", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre botones

        // Botón "Registrarse"
        Button(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp), // Aumentamos la altura del botón
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White // Texto blanco
            ),
            shape = MaterialTheme.shapes.medium // Borde redondeado
        ) {
            Text("Registrarse", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre botones

        // Botón "Ver Usuarios"
        Button(
            onClick = onShowUsersClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp), // Aumentamos la altura del botón
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = Color.White // Texto blanco
            ),
            shape = MaterialTheme.shapes.medium // Borde redondeado
        ) {
            Text("Ver Usuarios", style = MaterialTheme.typography.bodyLarge)
        }
    }
}