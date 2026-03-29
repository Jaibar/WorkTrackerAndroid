package com.example.worktracker
import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.worktracker.ui.MainScreen

class MainActivity : ComponentActivity() {
    private val perm = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        perm.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        setContent {
            MaterialTheme {
                var gps by remember { mutableStateOf(true) }
                var startDay by remember { mutableStateOf(6) }
                var showSettings by remember { mutableStateOf(false) }

                Scaffold(topBar = {
                    TopAppBar(
                        title = { Text(if (showSettings) "Settings" else "Work Tracker") },
                        actions = {
                            TextButton(onClick = { showSettings = !showSettings }) {
                                Text(if (showSettings) "Main" else "⚙️")
                            }
                        })
                }) { pad ->
                    if (showSettings)
                        SettingsScreen(gps,{gps=it},startDay,{startDay=it},Modifier.padding(pad))
                    else
                        MainScreen(this,gps,startDay)
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(gps:Boolean,onT:(Boolean)->Unit,start:Int,onS:(Int)->Unit,mod:Modifier=Modifier){
    var day by remember{mutableStateOf(start.toString())}
    Column(mod.padding(24.dp)){
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically){
            Text("Enable GPS");Spacer(Modifier.width(16.dp))
            Switch(checked=gps,onCheckedChange=onT)
        }
        Spacer(Modifier.height(24.dp))
        Text("Month start day:")
        TextField(value=day,onValueChange={
            day=it.filter{c->c.isDigit()};it.toIntOrNull()?.let(onS)
        },label={Text("1–28")})
    }
}