package com.anago.spviewer.compat

import android.content.Intent
import android.os.Build
import java.io.Serializable

object IntentCompat {
    inline fun <reified T : Serializable> Intent.getCSerializableExtra(name: String): T {
        return if (Build.VERSION.SDK_INT >= 33) {
            getSerializableExtra(name, T::class.java) as T
        } else {
            @Suppress("DEPRECATION")
            getSerializableExtra(name) as T
        }
    }
}