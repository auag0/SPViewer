package com.anago.spviewer.applist

import android.app.Application
import android.content.pm.ApplicationInfo.FLAG_SYSTEM
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.anago.spviewer.compat.PackageManagerCompat.getCInstalledApplications
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppListViewModel(private val app: Application) : AndroidViewModel(app) {
    private var appList: List<AppItem> = emptyList()
    private var displayedAppList: MutableLiveData<List<AppItem>> = MutableLiveData(emptyList())

    fun getDisplayedAppList(): LiveData<List<AppItem>> {
        return displayedAppList
    }

    fun fetchInstalledAppList() {
        viewModelScope.launch(Dispatchers.Default) {
            val pm = app.packageManager
            val installedApps = pm.getCInstalledApplications(0).map { appInfo ->
                AppItem(
                    name = appInfo.loadLabel(pm).toString(),
                    packageName = appInfo.packageName,
                    icon = appInfo.loadIcon(pm),
                    isSystem = appInfo.flags and FLAG_SYSTEM == FLAG_SYSTEM,
                    appInfo = appInfo
                )
            }
            appList = installedApps
            updateDisplayedAppList()
        }
    }

    private fun updateDisplayedAppList() {
        var displayedApps = appList

        // システムアプリを除外する
        displayedApps = displayedApps.filterNot { it.isSystem }

        // 名前順に並び替える
        displayedApps = displayedApps.sortedBy { it.name }

        displayedAppList.postValue(displayedApps)
    }

    init {
        fetchInstalledAppList()
    }
}