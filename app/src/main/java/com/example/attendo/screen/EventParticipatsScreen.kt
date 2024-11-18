package com.example.attendo.screen

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity

import com.example.attendo.DAO.AttendeeDao
import com.example.attendo.DAO.EventDao
import com.example.attendo.modelos.Attendee
import com.google.zxing.integration.android.IntentIntegrator
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.UnitValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.text.DecimalFormat
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventParticipantsScreen(
    eventId: Int,
    eventName: String,
    onBack: () -> Unit,
    attendeeDao: AttendeeDao,
    eventDao: EventDao
) {
    var attendees by remember { mutableStateOf(listOf<Attendee>()) }
    var filteredAttendees by remember { mutableStateOf(listOf<Attendee>()) }
    var searchText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var reportType by remember { mutableStateOf("Todos") }

    // Lógica para cargar los participantes desde la base de datos
    LaunchedEffect(eventId) {
        attendees = attendeeDao.getAttendeesForEvent(eventId)
        filteredAttendees = attendees // Iniciar con todos los participantes
    }

    // Filtrar los asistentes según el texto de búsqueda
    fun filterAttendees(query: String) {
        filteredAttendees = if (query.isEmpty()) {
            attendees
        } else {
            attendees.filter { it.name.contains(query, ignoreCase = true) }
        }
    }

    // Función para seleccionar el archivo Excel
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> uri?.let {
            coroutineScope.launch {
                attendeeDao.deleteAttendeesByEvent(eventId)
                processExcelFile(uri, context, eventId, attendeeDao)
                attendees = attendeeDao.getAttendeesForEvent(eventId)
                filterAttendees(searchText) // Aplicar filtro al actualizar
                Toast.makeText(context, "Participantes cargados exitosamente", Toast.LENGTH_SHORT).show()
            }
        }}
    )

    // Lógica para el escaneo de QR
    val scanLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanResult = result.data?.getStringExtra("SCAN_RESULT")
            scanResult?.let { cod ->
                coroutineScope.launch {
                    updateAttendeeAttendance(cod, eventId, attendeeDao, context)
                    attendees = attendeeDao.getAttendeesForEvent(eventId)
                    filterAttendees(searchText) // Aplicar filtro al actualizar
                }
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF0F0F0))
    ) {
        TopAppBar(
            title = { Text(eventName) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        )

        // TextField de búsqueda
        TextField(
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
                filterAttendees(newText)
            },
            label = { Text("Buscar participante") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Text("Participantes", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Encabezado de la tabla
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.primary.copy(alpha = 0.1f)),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Nombre",
                        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f).padding(vertical = 8.dp)
                    )
                    Text(
                        "Asistencia",
                        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(0.5f).padding(vertical = 8.dp)
                    )
                }
            }

            itemsIndexed(filteredAttendees) { index, attendee ->
                val backgroundColor = if (index % 2 == 0) {
                    MaterialTheme.colors.surface
                } else {
                    MaterialTheme.colors.onSurface.copy(alpha = 0.05f)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        attendee.name,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.weight(1f)
                    )
                    Checkbox(
                        checked = attendee.attendance,
                        onCheckedChange = null,
                        enabled = false,
                        modifier = Modifier.weight(0.5f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botones en la parte inferior
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Button(onClick = { launcher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") },
                modifier = Modifier.fillMaxWidth()) {
                Text("Cargar Excel de Participantes")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = { scanLauncher.launch(IntentIntegrator(context as Activity).createScanIntent()) }) {
                    Text("Escanear QR")
                }

                Button(onClick = {
                    coroutineScope.launch {
                        attendeeDao.deleteAttendeesByEvent(eventId)
                        attendees = attendeeDao.getAttendeesForEvent(eventId)
                        filterAttendees(searchText) // Aplicar filtro después de eliminar
                        Toast.makeText(context, "Todos los participantes han sido eliminados", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Eliminar Datos")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (attendees.isNotEmpty()) {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Generar Reporte")
                }
            }
        }

        // Diálogo para selección de tipo de reporte
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Seleccionar tipo de reporte") },
                text = {
                    Column {
                        RadioButtonWithText("Todos", reportType) { reportType = "Todos" }
                        RadioButtonWithText("Presentes", reportType) { reportType = "Presentes" }
                        RadioButtonWithText("Ausentes", reportType) { reportType = "Ausentes" }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        generatePdfReport(attendees, reportType, eventId.toString(), context)
                    }) {
                        Text("Generar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}


// Función para actualizar la asistencia del participante
private suspend fun updateAttendeeAttendance(
    cod: String,
    eventId: Int,
    attendeeDao: AttendeeDao,
    context: Context
) {
    try {
        // Convertir el código escaneado a String, en caso de que sea necesario
        val codString = cod.toString()

        // Intentar obtener el participante específico para el evento
        val attendee = attendeeDao.getAttendeeByCodAndEvent(codString, eventId)
        if (attendee == null) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "No se encontró un participante con el código: $codString para este evento", Toast.LENGTH_SHORT).show()
            }
            return
        }

        // Actualizar la asistencia
        val updatedAttendee = attendee.copy(attendance = true)
        attendeeDao.updateAttendee(updatedAttendee)

        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Asistencia actualizada para el participante: $codString", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Error al actualizar la asistencia: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}



fun processExcelFile(uri: Uri, context: Context, eventId: Int, attendeeDao: AttendeeDao) {
    val coroutineScope = CoroutineScope(Dispatchers.IO)  // Usar un CoroutineScope con IO dispatcher

    coroutineScope.launch {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val workbook = XSSFWorkbook(inputStream)

            // Asumiendo que la primera hoja contiene los datos de los participantes
            val sheet = workbook.getSheetAt(0)

            // Iterar sobre las filas del archivo Excel
            for (rowIndex in 1 until sheet.physicalNumberOfRows) {
                val row = sheet.getRow(rowIndex)

                // Leer las celdas de la fila
                val codCell = row.getCell(0)
                val nameCell = row.getCell(1)
                val emailCell = row.getCell(2)

                // Obtener el valor de la celda 'cod' como String (como texto en lugar de numérico)
                val cod = when (codCell.cellType) {
                    CellType.STRING -> codCell.stringCellValue
                    CellType.NUMERIC -> {
                        if (codCell.numericCellValue % 1 == 0.0) {
                            codCell.numericCellValue.toLong().toString()
                        } else {
                            val stringValue = codCell.toString()
                            if (stringValue.contains("E")) {
                                val decimalFormat = DecimalFormat("#")
                                decimalFormat.format(codCell.numericCellValue)
                            } else {
                                stringValue
                            }
                        }
                    }
                    else -> ""
                }

                // Verificar si el participante ya existe en este evento
                val existingAttendee = attendeeDao.getAttendeeByCodAndEventId(cod, eventId)
                if (existingAttendee != null) {
                    // Si ya existe en este evento, no lo insertamos de nuevo
                    continue
                }

                // Obtener el valor de la celda 'name' como String
                val name = when (nameCell.cellType) {
                    CellType.STRING -> nameCell.stringCellValue
                    CellType.NUMERIC -> nameCell.numericCellValue.toString()
                    else -> ""
                }

                // Obtener el valor de la celda 'email' como String
                val email = when (emailCell.cellType) {
                    CellType.STRING -> emailCell.stringCellValue
                    CellType.NUMERIC -> emailCell.numericCellValue.toString()
                    else -> ""
                }

                // Crear el participante
                val attendee = Attendee(
                    cod = cod,
                    name = name,
                    email = email,
                    eventId = eventId
                )

                // Insertar el participante en la base de datos
                attendeeDao.insertAttendee(attendee)
            }

            workbook.close()

            // Volver al hilo principal para mostrar el Toast
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Participantes cargados exitosamente", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()

            // Volver al hilo principal para mostrar el Toast en caso de error
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error al cargar el archivo Excel: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


//COmposable del radio buton de reporte
@Composable
fun RadioButtonWithText(text: String, selectedValue: String, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = (text == selectedValue), onClick = onClick)
        Text(text = text, modifier = Modifier.clickable(onClick = onClick))
    }
}
//Funsion que genera el reporte
fun generatePdfReport(
    attendees: List<Attendee>,
    reportType: String,
    eventName: String,   // Nuevo parámetro
    context: Context
) {
    val filteredAttendees = when (reportType) {
        "Presentes" -> attendees.filter { it.attendance }
        "Ausentes" -> attendees.filter { !it.attendance }
        else -> attendees
    }

    // Define la ubicación del archivo en la carpeta de Descargas con el nombre del evento
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val sanitizedEventName = eventName.replace(Regex("[^a-zA-Z0-9]"), "_") // Elimina caracteres no válidos en nombres de archivo
    val pdfFile = File(downloadsDir, "reporte_${sanitizedEventName}_${reportType}_${System.currentTimeMillis()}.pdf")
    val pdfWriter = PdfWriter(pdfFile)
    val pdfDocument = PdfDocument(pdfWriter)
    val document = Document(pdfDocument)

    // Agrega título y tabla al PDF
    document.add(Paragraph("Reporte de Asistencia - $reportType"))
    val table = Table(UnitValue.createPercentArray(floatArrayOf(1f, 3f, 2f)))
    table.addHeaderCell("ID")
    table.addHeaderCell("Nombre")
    table.addHeaderCell("Asistencia")

    filteredAttendees.forEach { attendee ->
        table.addCell(attendee.cod.toString())
        table.addCell(attendee.name)
        table.addCell(if (attendee.attendance) "Presente" else "No Presente")
    }

    document.add(table)
    document.close()

    // Mensaje de éxito
    Toast.makeText(context, "PDF guardado en Descargas: ${pdfFile.path}", Toast.LENGTH_LONG).show()
}



