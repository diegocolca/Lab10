package com.example.lab10

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lab10.model.SerieModel
import com.example.lab10.service.SerieApiService
import kotlinx.coroutines.delay
import java.util.Calendar

@Composable
fun ContenidoSerieEditar(navController: NavHostController, servicio: SerieApiService, pid: Int = 0 ) {
    var id by remember { mutableStateOf<Int>(pid) }
    var name by remember { mutableStateOf<String?>("") }
    var release_date by remember { mutableStateOf<String?>("") }
    var rating by remember { mutableStateOf<String?>("") }
    var category by remember { mutableStateOf<String?>("") }
    var grabar by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                release_date = formattedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    if (id != 0) {
        LaunchedEffect(Unit) {
            val objSerie = servicio.selectSerie(id.toString())
            delay(100)
            name = objSerie.body()?.name
            release_date = objSerie.body()?.release_date ?:""
            rating = objSerie.body()?.rating.toString()
            category = objSerie.body()?.category
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        // Spacer(Modifier.height(50.dp))
        TextField(
            value = id.toString(),
            onValueChange = { },
            label = { Text("ID (solo lectura)") },
            readOnly = true,
            singleLine = true
        )
        TextField(
            value = name!!,
            onValueChange = { name = it },
            label = { Text("Name: ") },
            singleLine = true
        )
        TextField(
            value = release_date!!,
            onValueChange = { release_date = it },
            label = { Text("Release Date:") },
            singleLine = true
        )
//        OutlinedTextField(
//            value = release_date ?: "",
//            onValueChange = {}, // no editable manualmente
//            label = { Text("Release Date:") },
//            readOnly = true,
//            singleLine = true,
//            modifier = Modifier
//                .clickable { datePickerDialog.show() }
//                .fillMaxWidth()
//        )
        TextField(
            value = rating!!,
            onValueChange = { rating = it },
            label = { Text("Rating:") },
            singleLine = true
        )
        TextField(
            value = category!!,
            onValueChange = { category = it },
            label = { Text("Category:") },
            singleLine = true
        )
        Button(
            onClick = {
                grabar = true
            }
        ) {
            Text("Grabar", fontSize=16.sp)
        }
    }

    if (grabar) {
        val objSerie = SerieModel(id,name!!, release_date!!, rating!!.toInt(), category!!)
        LaunchedEffect(Unit) {
            if (id == 0)
                servicio.insertSerie(objSerie)
            else
                servicio.updateSerie(id.toString(), objSerie)
        }
        grabar = false
        navController.navigate("series")
    }
}