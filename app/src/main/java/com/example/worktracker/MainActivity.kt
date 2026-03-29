package com.example.worktracker

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.worktracker.ui.MainScreen

class MainActivity : ComponentActivity() {
    private val perm =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        perm.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        setContent {
            MaterialTheme {
                var gpsEnabled by remember { mutableStateOf(true) }
                var monthStart by remember { mutableStateOf(6) }
                var showSettings by remember { mutableStateOf(false) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(if (showSettings) "Settings" else "Work Tracker") },
                            actions = {
                                TextButton(onClick = { showSettings = !showSettings }) {
                                    Text(if (showSettings) "Main" else "⚙️")
                                }
                            }
                        )
                    }
                ) { padding ->
                    Box(Modifier.padding(padding)) {
                        if (showSettings)
                            SettingsScreen(
                                gpsEnabled,
                                { gpsEnabled = it },
                                monthStart,
                                { monthStart = it }
                            )
                        else
                            MainScreen(this@MainActivity, gpsEnabled, monthStart)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    gpsEnabled: Boolean,
    onToggleGps: (Boolean) -> Unit,
    monthStart: Int,
    onChangeDay: (Int) -> Unit
) {
    var day by remember { mutableStateOf(monthStart.toString()) }

    Column(Modifier.padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Enable GPS")
            Spacer(Modifier.width(16.dp))
            Switch(checked = gpsEnabled, onCheckedChange = onToggleGps)
        }
        Spacer(Modifier.height(24.dp))
        Text("Month start day:")
        TextField(
            value = day,
            onValueChange = {
                day = it.filter(Char::isDigit)
                it.toIntOrNull()?.let(onChangeDay)
            },
            label = { Text("1–28") }
        )
    }
}
