package com.oduit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.oduit.app.navigation.OduitNavGraph
import com.oduit.app.ui.theme.OduitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OduitTheme {
                OduitNavGraph()
            }
        }
    }
}
