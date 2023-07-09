package com.anago.spviewer

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.google.android.material.color.DynamicColors
import com.topjohnwu.superuser.Shell

class MyApp : Application() {
    companion object {
        private var toast: Toast? = null

        fun makeToast(context: Context, msg: String) {
            toast?.cancel()
            toast = Toast.makeText(context, msg, Toast.LENGTH_LONG)
            toast?.show()
        }
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        Shell.enableVerboseLogging = true
    }
}