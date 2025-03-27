package com.example.miles

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class MilesApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this) // Initialize Firebase
        Log.d("FirebaseCheck", "Firebase Initialized: ${FirebaseApp.getApps(this).isNotEmpty()}")
    }
}
