package com.anago.spviewer.compat

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build

object PackageManagerCompat {
    fun PackageManager.getCInstalledApplications(flags: Int): List<ApplicationInfo> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getInstalledApplications(PackageManager.ApplicationInfoFlags.of(flags.toLong()))
        } else {
            getInstalledApplications(flags)
        }
    }
}