package com.sachinsandbhor.firebasesample

import android.app.Application
import com.google.firebase.FirebaseApp

class FirebaseSampleApp: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}