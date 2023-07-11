package com.anago.spviewer.compat

import android.os.Build
import android.os.Bundle
import java.io.Serializable

object BundleCompat {
    inline fun <reified T : Serializable> Bundle.getCSerializable(key: String): T? {
        return if (Build.VERSION.SDK_INT >= 33) {
            getSerializable(key, T::class.java)
        } else {
            @Suppress("DEPRECATION") getSerializable(key) as T?
        }
    }
}