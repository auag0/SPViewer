package com.anago.spviewer.models

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo

data class AppItem(
    val packageInfo: PackageInfo,
    val name: String,
    val packageName: String,
    val installTime: Long,
    val updateTime: Long
) {
    fun isSystemApp(): Boolean {
        val appInfo = packageInfo.applicationInfo
        return appInfo.flags and ApplicationInfo.FLAG_SYSTEM == ApplicationInfo.FLAG_SYSTEM
    }
}