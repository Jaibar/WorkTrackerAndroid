package com.example.worktracker.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.worktracker.viewmodel.WorkViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.tasks.await

@SuppressLint("MissingPermission")
@Composable
fun MainScreen(context: Context, gpsEnabled: Boolean, monthStartDay: Int) {
    val vm: WorkViewModel = viewModel(factory = androidx.lifecycle.viewmodel.initializer {
        WorkViewModel(context)
    })

    val scope = rememberCoroutineScope()
    val isWorking by vm.isWorking.collectAsState()
    val startTime by vm.startTime.collectAsState()

    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    var slider by remember { mutableStateOf(0f) }
    val fused = remember { LocationServices.getFusedLocationProviderClient(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Work Tracker", style = MaterialTheme.typography.headlineSmall)
        Text("Now: ${LocalDateTime.now().format(formatter)}")

        if (isWorking && startTime != null) {
            val dur = Duration.between(startTime, LocalDateTime.now()).toMinutes()
            Text("Elapsed: %02d:%02d".format(dur / 60, dur % 60))
        }

        Spacer(Modifier.height(16.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, drag ->
                        slider = (slider + drag / 300f).coerceIn(-1f, 1f)
                        if (slider <= -0.9f && !isWorking) {
                            slider = 0f
                            scope.launch {
                                val loc = if (gpsEnabled)
                                    fused.lastLocation.await()
                                else null
                                vm.startShift(loc?.latitude, loc?.longitude)
                            }
                        } else if (slider >= 0.9f && isWorking) {
                            slider = 0f
                            scope.launch {
                                val loc = if (gpsEnabled)
                                    fused.lastLocation.await()
                                else null
                                vm.endShift(loc?.latitude, loc?.longitude)
                            }
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val x = size.width / 2 + slider * size.width / 2.5f
                drawRoundRect(Color.LightGray)
                drawCircle(Color.Cyan, 30f, Offset(x, size.height / 2))
            }
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Begin")
                Text("End")
            }
        }
    }
}
