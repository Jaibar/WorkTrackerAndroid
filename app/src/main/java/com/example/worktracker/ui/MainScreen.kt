package com.example.worktracker.ui
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.worktracker.viewmodel.WorkViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.time.*

@SuppressLint("MissingPermission")
@Composable
fun MainScreen(context: Context, gps: Boolean, startDay: Int) {
    val vm: WorkViewModel = viewModel(factory = androidx.lifecycle.viewmodel.initializer {
        WorkViewModel(context)
    })
    val scope = rememberCoroutineScope()
    val working by vm.isWorking.collectAsState()
    val startTime by vm.startTime.collectAsState()

    val fused = LocationServices.getFusedLocationProviderClient(context)
    var slider by remember { mutableStateOf(0f) }

    Column(Modifier.padding(24.dp)) {
        Text("Work Tracker", style = MaterialTheme.typography.headlineSmall)
        if (working && startTime != null) {
            val mins = Duration.between(startTime, LocalDateTime.now()).toMinutes()
            Text("Elapsed: %02d:%02d".format(mins/60, mins%60))
        }

        Box(Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dr ->
                    slider = (slider + dr/300f).coerceIn(-1f, 1f)
                    if (slider <= -0.9f && !working) {
                        slider = 0f
                        scope.launch {
                            val l = if (gps) fused.lastLocation.await() else null
                            vm.startShift(l?.latitude,l?.longitude)
                        }
                    } else if (slider >= 0.9f && working) {
                        slider = 0f
                        scope.launch {
                            val l = if (gps) fused.lastLocation.await() else null
                            vm.endShift(l?.latitude,l?.longitude)
                        }
                    }
                }
            }) {
            Canvas(Modifier.fillMaxSize()) {
                val x = size.width/2 + slider * size.width/2.5f
                drawRoundRect(androidx.compose.ui.graphics.Color.LightGray)
                drawCircle(androidx.compose.ui.graphics.Color.Cyan,30f,Offset(x,size.height/2))
            }
            Row(Modifier.fillMaxSize().padding(16.dp),Arrangement.SpaceBetween) {
                Text("Begin"); Text("End")
            }
        }
    }
}