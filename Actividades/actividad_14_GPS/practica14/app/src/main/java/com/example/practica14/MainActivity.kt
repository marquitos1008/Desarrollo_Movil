package com.example.practica14

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", 0))
        Configuration.getInstance().userAgentValue = "Practica14_Marco_UAS/1.0"

        setContent {
            MapaOSMScreen()
        }
    }
}