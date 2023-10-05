package com.anago.spviewer.viewmodels

import android.app.Application
import android.content.pm.ApplicationInfo.FLAG_SYSTEM
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.anago.spviewer.compat.PackageManagerCompat.getCInstalledApplications
import com.anago.spviewer.models.AppItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppListViewModel(private val app: Application) : AndroidViewModel(app) {
    private var appList: List<AppItem> = emptyList()
    private var _displayedAppList: MutableLiveData<List<AppItem>> = MutableLiveData(emptyList())
    val displayedAppList: LiveData<List<AppItem>> = _displayedAppList
    private var _isRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing
    var searchQuery: String? = null
        set(value) {
            field = value
            updateDisplayedAppList()
        }

    init {
        fetchInstalledAppList()
    }

    fun fetchInstalledAppList() {
        viewModelScope.launch(Dispatchers.Default) {
            _isRefreshing.postValue(true)
            val pm = app.packageManager
            val installedApps = pm.getCInstalledApplications(0).map { appInfo ->
                val isSystem = appInfo.flags and FLAG_SYSTEM == FLAG_SYSTEM
                AppItem(
                    name = appInfo.loadLabel(pm).toString(),
                    packageName = appInfo.packageName,
                    icon = appInfo.loadIcon(pm),
                    isSystem = isSystem
                )
            }
            appList = installedApps
            updateDisplayedAppList()
        }
    }

    private fun updateDisplayedAppList() {
        viewModelScope.launch(Dispatchers.Default) {
            val displayedApps = appList.filterNot { it.isSystem }.let { apps ->
                if (searchQuery.isNullOrBlank()) {
                    apps.sortedBy { it.name }
                } else {
                    apps.filter { it.name.contains(searchQuery!!, true) }.sortedBy {
                        it.name.indexOf(searchQuery!!, ignoreCase = true)
                    }
                }
            }
            _displayedAppList.postValue(displayedApps)
            _isRefreshing.postValue(false)
        }
    }
}