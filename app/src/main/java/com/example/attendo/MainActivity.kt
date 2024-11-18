package com.example.attendo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import androidx.room.Room
import com.example.attendo.DAO.AppDatabase
import com.example.attendo.DAO.AttendeeDao
import com.example.attendo.modelos.User
import com.example.attendo.screen.CrearUsuarioScreen
import com.example.attendo.ui.theme.AttendoTheme
//import com.example.attendo.screen.UsuariosScreen
import com.example.attendo.DAO.UserDao
import com.example.attendo.screen.CreateEventModal
import com.example.attendo.screen.EventScreen
import com.example.attendo.screen.LoginScreen
//import com.example.attendo.screen.PasswordResetScreen
import com.example.attendo.screen.RegisterScreen
//import com.example.attendo.screen.UserListScreen
import com.example.attendo.screen.UserTable
import com.example.attendo.screen.WelcomeScreen
import com.example.attendo.screen.RegisterScreen


import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var attendeeDao: AttendeeDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa la base de datos y el DAO
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "attendo-db"
        ).fallbackToDestructiveMigration()
            .build()
        userDao = database.userDao()
        attendeeDao = database.attendeeDao()

        setContent {
            AttendoTheme {
                var showLoginScreen by remember { mutableStateOf(false) }
                var showRegisterScreen by remember { mutableStateOf(false) }
                var showEventsScreen by remember { mutableStateOf(false) }
                var showCreateEventModal by remember { mutableStateOf(false) } // Cambia aquí para el modal
                var showUserTableScreen by remember { mutableStateOf(false) }
                var showPasswordResetScreen by remember { mutableStateOf(false) }


                var currentUserId by remember { mutableStateOf<Int?>(null) }
                // Muestra la pantalla correspondiente según el estado
                when {


                    showEventsScreen -> {
                        if (currentUserId != null) {
                            val context = LocalContext.current
                            val appDatabase = AppDatabase.getDatabase(context)
                            val eventDao = appDatabase.eventDao()
                            //val currentUserId =1 // Aquí debes obtener el ID del usuario logueado dinámicamente
                            EventScreen(
                                eventDao = eventDao,
                                userId = currentUserId!!,
                                attendeeDao = attendeeDao,
                                onLogout = {
                                    showEventsScreen = false
                                    showLoginScreen = true
                                }
                            )
                        }


                    }

                    showLoginScreen -> {
                        LoginScreen(
                            userDao = userDao,
                            onLoginSuccess = { userId ->
                                currentUserId = userId // Actualiza el ID del usuario después de iniciar sesión
                                showEventsScreen = true
                                showLoginScreen = false
                            },
                            onBack = { showLoginScreen = false }
                            //onForgotPassword = {
                            //    showPasswordResetScreen = true
                            //    showLoginScreen = false
                            //} // Cambia la pantalla a la de recuperación de contraseña
                        )
                    }


                    showRegisterScreen -> {
                        RegisterScreen(userDao = userDao, onRegisterSuccess = {
                            showLoginScreen = true
                            showRegisterScreen = false
                        },
                            onBackPressed = {
                                showRegisterScreen = false
                            }
                        )
                    }

                    showUserTableScreen -> {

                        UserTable(userDao = userDao,
                            onBack = {showUserTableScreen = false}) // Asegúrate de pasar el userDao aquí
                    }

                    else -> {
                        // Pantalla principal con botones de iniciar sesión, registrarse y ver usuarios
                        WelcomeScreen(
                            onLoginClick = { showLoginScreen = true },
                            onRegisterClick = { showRegisterScreen = true },
                            onShowUsersClick = { showUserTableScreen = true }
                        )
                    }
                }
            }
        }
    }
}
