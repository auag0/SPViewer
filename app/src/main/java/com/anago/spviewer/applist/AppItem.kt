package com.anago.spviewer.applist

import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable

data class AppItem(
    val name: String,
    val packageName: String,
    val icon: Drawable,
    val isSystem: Boolean,
    val appInfo: ApplicationInfo
)