package com.anago.spviewer.utils

object NumberUtils {
    fun Any.toBooleanEasy(): Boolean {
        return when (this.toString().lowercase()) {
            "true", "1", "1.0" -> true
            "false", "0", "0.0" -> false
            else -> throw IllegalArgumentException()
        }
    }
}