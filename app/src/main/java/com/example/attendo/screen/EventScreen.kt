package com.example.attendo.screen

import android.widget.Toast
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
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.attendo.DAO.AppDatabase
import com.example.attendo.DAO.AttendeeDao
import com.example.attendo.DAO.EventDao
import com.example.attendo.modelos.Event
import kotlinx.coroutines.launch
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun EventScreen(
    eventDao: EventDao,
    userId: Int,
    attendeeDao: AttendeeDao,
    onLogout: () -> Unit
) {
    var events by remember { mutableStateOf(listOf<Event>()) }
    val coroutineScope = rememberCoroutineScope()
    var showCreateEventModal by remember { mutableStateOf(false) }
    val navController = rememberNavController()

    var searchQuery by remember { mutableStateOf("") }  // Variable para la búsqueda
    // SnackbarHostState para mostrar el mensaje
    val snackbarHostState = remember { SnackbarHostState() }

    // Verificación en LaunchedEffect
    LaunchedEffect(userId) {
        coroutineScope.launch {
            events = eventDao.getEventsByUserId(userId)
            println("Eventos cargados: ${events.size}")
        }
    }

    val filteredEvents = events.filter { event ->
        event.name.contains(searchQuery, ignoreCase = true)
    }

    NavHost(navController = navController, startDestination = "events_screen") {
        composable("events_screen") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Usuario ID: $userId", style = MaterialTheme.typography.body2, color = Color.Gray)

                Text(
                    "Eventos",
                    style = MaterialTheme.typography.h4.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colors.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de búsqueda
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar eventos") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de crear evento estilizado
                Button(
                    onClick = { showCreateEventModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colors.secondary),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Crear Evento",
                        style = MaterialTheme.typography.button.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredEvents) { event ->
                        EventItem(
                            event = event,
                            onViewParticipantsClick = {
                                navController.navigate("event_participants_screen/${event.id}")
                            },
                            onDeleteEvent = { eventId ->
                                coroutineScope.launch {
                                    val attendeeCount = attendeeDao.getAttendeeCountForEvent(eventId)
                                    if (attendeeCount > 0) {
                                        // Mostrar mensaje con Snackbar si el evento tiene asistentes
                                        snackbarHostState.showSnackbar("No se puede eliminar el evento porque tiene asistentes registrados.")
                                    } else {
                                        // Eliminar el evento si no tiene asistentes
                                        eventDao.deleteEventById(eventId)
                                        events = eventDao.getEventsByUserId(userId)  // Actualizar la lista de eventos
                                        snackbarHostState.showSnackbar("Evento eliminado exitosamente.")
                                    }
                                }
                            }
                        )
                    }
                    if (filteredEvents.isEmpty()) {
                        item {
                            Text(
                                "No hay eventos que coincidan con la búsqueda.",
                                style = MaterialTheme.typography.body1.copy(fontStyle = FontStyle.Italic)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de cerrar sesión estilizado
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colors.error),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Cerrar Sesión",
                        style = MaterialTheme.typography.button.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                if (showCreateEventModal) {
                    CreateEventModal(
                        userId = userId,
                        onEventCreated = { event ->
                            coroutineScope.launch {
                                eventDao.insertEvent(event)
                                events = eventDao.getEventsByUserId(userId)
                            }
                            showCreateEventModal = false
                        },
                        onDismiss = { showCreateEventModal = false }
                    )
                }
            }
        }

        composable("event_participants_screen/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toInt() ?: 0
            val context = LocalContext.current
            val eventDao = AppDatabase.getDatabase(context).eventDao()
            val eventName = remember { mutableStateOf("") }

            LaunchedEffect(eventId) {
                eventName.value = eventDao.getEventNameById(eventId)
            }

            EventParticipantsScreen(
                eventId = eventId,
                eventName = eventName.value,
                attendeeDao = attendeeDao,
                eventDao = eventDao,
                onBack = { navController.popBackStack() }
            )
        }
    }
}


@Composable
fun EventItem(event: Event, onViewParticipantsClick: (Int) -> Unit, onDeleteEvent: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 6.dp,
        shape = MaterialTheme.shapes.large // Esquinas más suaves
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Evento: ${event.name}", style = MaterialTheme.typography.h6.copy(fontFamily = FontFamily.Serif))
            Text("Fecha: ${event.date}", style = MaterialTheme.typography.body2)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { onViewParticipantsClick(event.id) },  // Llamada a la función de navegación con el ID del evento
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text(
                        "Ver Participantes",
                        style = MaterialTheme.typography.button.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                IconButton(onClick = { onDeleteEvent(event.id) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar evento", tint = Color.Red)
                }
            }
        }
    }
}


@Composable
fun CreateEventModal(
    userId: Int,
    onEventCreated: (Event) -> Unit, // Callback cuando se crea el evento
    onDismiss: () -> Unit            // Callback para cerrar el modal
) {
    var eventName by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Crear Nuevo Evento", style = MaterialTheme.typography.h6) },
        text = {
            Column {
                TextField(
                    value = eventName,
                    onValueChange = { eventName = it },
                    label = { Text("Nombre del Evento") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = eventDate,
                    onValueChange = { eventDate = it },
                    label = { Text("Fecha del Evento") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newEvent = Event(
                        name = eventName,
                        date = eventDate,
                        userId = userId
                    )
                    onEventCreated(newEvent) // Llama al callback para crear el evento
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colors.primary) // Para Material2
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colors.secondary) // Para Material2
            ) {
                Text("Cancelar")
            }
        }
    )
}



//@Composable
//fun EventScreen(onLogout: () -> Unit) {
 //   Column(
  //      modifier = Modifier
   //         .fillMaxSize()
  //          .padding(16.dp),
   //     verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
 //   ) {
  //      Text("Eventos", style = MaterialTheme.typography.h5)
   //     Spacer(modifier = Modifier.height(16.dp))
   //     Button(onClick = onLogout) {
   //         Text("Cerrar Sesión")
   //     }
   // }
//}
