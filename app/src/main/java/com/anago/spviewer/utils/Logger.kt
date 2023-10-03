package com.anago.spviewer.utils

import android.util.Log

object Logger {
    private const val TAG = "SPViewer"

    fun debug(msg: Any?) {
        Log.d(TAG, msg.toString())
    }

    fun error(msg: Any?) {
        Log.d(TAG, msg.toString())
    }
}