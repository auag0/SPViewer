package com.anago.spviewer.settings

import android.content.Context
import androidx.annotation.IntDef
import com.anago.spviewer.settings.PrefManager.getPrefs

object AppListPrefs {
    @IntDef(
        SORT_APP_NAME,
        SORT_APP_PACKAGE_NAME,
        SORT_APP_INSTALL_TIME,
        SORT_APP_UPDATE_TIME
    )
    annotation class Sort

    const val SORT_APP_NAME = 0
    const val SORT_APP_PACKAGE_NAME = 1
    const val SORT_APP_INSTALL_TIME = 2
    const val SORT_APP_UPDATE_TIME = 3

    private const val PREFS_APP_LIST_SORT = "app_list_sort"

    @Sort
    fun getAppListSort(context: Context): Int {
        return getPrefs(context).getInt(PREFS_APP_LIST_SORT, SORT_APP_NAME)
    }

    fun setAppListSort(context: Context, @Sort sort: Int) {
        getPrefs(context).edit().putInt(PREFS_APP_LIST_SORT, sort).commit()
    }
}