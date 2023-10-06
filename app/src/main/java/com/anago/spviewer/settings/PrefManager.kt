package com.anago.spviewer.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object PrefManager {
    private var sharedPreferences: SharedPreferences? = null
    fun getPrefs(context: Context): SharedPreferences {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        }
        return sharedPreferences!!
    }
}