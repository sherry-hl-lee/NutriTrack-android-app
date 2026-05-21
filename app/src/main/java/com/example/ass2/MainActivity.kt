package com.example.ass2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ass2.auth.GoogleAuthManager
import com.example.ass2.ui.navigation.AppNavHost

class MainActivity : ComponentActivity() {

    private lateinit var googleAuthManager: GoogleAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        googleAuthManager = GoogleAuthManager(this)

        setContent {
            AppNavHost(googleAuthManager = googleAuthManager)
        }
    }
}
