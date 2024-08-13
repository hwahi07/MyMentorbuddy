package com.example.mymentorbuddy

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApplication: Application() {
    class MyApplication : Application() {
        override fun onCreate() {
            super.onCreate()
            FirebaseApp.initializeApp(this)
        }
    }
}