package com.anago.spviewer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.anago.spviewer.compat.PackageManagerCompat.getCInstalledPackages
import com.anago.spviewer.models.AppItem
import com.anago.spviewer.settings.AppListPrefs
import com.anago.spviewer.settings.AppListPrefs.SORT_APP_INSTALL_TIME
import com.anago.spviewer.settings.AppListPrefs.SORT_APP_NAME
import com.anago.spviewer.settings.AppListPrefs.SORT_APP_PACKAGE_NAME
import com.anago.spviewer.settings.AppListPrefs.SORT_APP_UPDATE_TIME
import com.anago.spviewer.settings.AppListPrefs.getAppListSort
import com.anago.spviewer.settings.AppListPrefs.setAppListSort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppListViewModel(private val app: Application) : AndroidViewModel(app) {
    private var appList: List<AppItem> = emptyList()
    private var _displayedAppList: MutableLiveData<List<AppItem>> = MutableLiveData(emptyList())
    val displayedAppList: LiveData<List<AppItem>> = _displayedAppList
    private var _isRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    @AppListPrefs.Sort
    var sort: Int
        get() = getAppListSort(app)
        set(value) {
            setAppListSort(app, value)
            updateDisplayedAppList()
        }
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
            val installedApps = pm.getCInstalledPackages(0).map { packageInfo ->
                val appInfo = packageInfo.applicationInfo
                AppItem(
                    packageInfo = packageInfo,
                    name = appInfo.loadLabel(pm).toString(),
                    packageName = appInfo.packageName,
                    installTime = packageInfo.firstInstallTime,
                    updateTime = packageInfo.lastUpdateTime
                )
            }
            appList = installedApps
            updateDisplayedAppList()
        }
    }

    private fun updateDisplayedAppList() {
        viewModelScope.launch(Dispatchers.Default) {
            _isRefreshing.postValue(true)
            val displayedApps = appList.filterNot { it.isSystemApp() }.let { apps ->
                if (searchQuery.isNullOrBlank()) {
                    when (sort) {
                        SORT_APP_NAME -> apps.sortedBy { it.name }
                        SORT_APP_PACKAGE_NAME -> apps.sortedBy { it.packageName }
                        SORT_APP_INSTALL_TIME -> apps.sortedBy { it.installTime }.reversed()
                        SORT_APP_UPDATE_TIME -> apps.sortedBy { it.updateTime }.reversed()
                        else -> apps
                    }
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