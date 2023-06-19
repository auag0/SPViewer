package com.anago.spviewer

import android.app.Application
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

class MyApp : Application() {
    companion object {
        var toast: Toast? = null
            set(value) {
                field?.cancel()
                field = value
            }
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}