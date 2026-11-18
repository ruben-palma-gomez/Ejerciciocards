package com.example.ejerciciocards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Tarea(
    val id: Int,
    val nombre: String,
    val hecha: Boolean,
    val nivel: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ListaDeTareas()
            }
        }
    }
}

@Composable
fun ListaDeTareas() {
    val tareas = remember {
        mutableStateListOf(
            Tarea(1, "Preparar el examen de Kotlin", false, "Alta"),
            Tarea(2, "Ir al gimnasio", false, "Baja"),
            Tarea(3, "Lavar la ropa", false, "Media"),
            Tarea(4, "Leer un capítulo del libro", true, "Baja"),
            Tarea(5, "Pagar la factura del móvil", true, "Alta")
        )
    }

    fun cambiarTarea(nueva: Tarea) {
        val posicion = tareas.indexOfFirst { tarea -> tarea.id == nueva.id }
        tareas[posicion] = nueva
    }

    val pendientes = tareas.filter { tarea -> !tarea.hecha }
    val hechas = tareas.filter { tarea -> tarea.hecha }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Mis tareas", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Text("Por hacer", fontSize = 20.sp)
        }

        items(pendientes, key = { tarea -> tarea.id }) { tarea ->
            TarjetaTarea(tarea, alCambiar = { nueva -> cambiarTarea(nueva) })
        }

        item {
            HorizontalDivider(
                thickness = 3.dp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            Text("Hechas", fontSize = 20.sp)
        }

        items(hechas, key = { tarea -> tarea.id }) { tarea ->
            TarjetaTarea(tarea, alCambiar = { nueva -> cambiarTarea(nueva) })
        }
    }
}

@Composable
fun TarjetaTarea(tarea: Tarea, alCambiar: (Tarea) -> Unit) {
    var menuCompletarAbierto by remember { mutableStateOf(false) }
    var menuNivelAbierto by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (tarea.hecha) Color(0xFFE8F5E9) else Color(0xFFFFF8E1)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarea.hecha,
                onCheckedChange = { marcada -> alCambiar(tarea.copy(hecha = marcada)) }
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    tarea.nombre,
                    fontSize = 17.sp,
                    textDecoration = if (tarea.hecha) TextDecoration.LineThrough else null
                )

                Spacer(modifier = Modifier.height(14.dp))

                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = colorNivel(tarea.nivel),
                            contentColor = Color.White
                        ) {
                            Text(tarea.nivel)
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Prioridad ${tarea.nivel}",
                        tint = colorNivel(tarea.nivel)
                    )
                }
            }

            Box {
                IconButton(onClick = { menuCompletarAbierto = true }) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Completar")
                }
                DropdownMenu(
                    expanded = menuCompletarAbierto,
                    onDismissRequest = { menuCompletarAbierto = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Completar tarea") },
                        onClick = {
                            alCambiar(tarea.copy(hecha = true))
                            menuCompletarAbierto = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Volver a pendiente") },
                        onClick = {
                            alCambiar(tarea.copy(hecha = false))
                            menuCompletarAbierto = false
                        }
                    )
                }
            }

            Box {
                IconButton(onClick = { menuNivelAbierto = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Cambiar prioridad")
                }
                DropdownMenu(
                    expanded = menuNivelAbierto,
                    onDismissRequest = { menuNivelAbierto = false }
                ) {
                    for (nivel in listOf("Alta", "Media", "Baja")) {
                        DropdownMenuItem(
                            text = { Text("Prioridad $nivel") },
                            onClick = {
                                alCambiar(tarea.copy(nivel = nivel))
                                menuNivelAbierto = false
                            }
                        )
                    }
                }
            }
        }
    }
}

fun colorNivel(nivel: String): Color {
    return when (nivel) {
        "Alta" -> Color(0xFFE53935)
        "Media" -> Color(0xFFFB8C00)
        else -> Color(0xFF43A047)
    }
}