package com.anago.spviewer.ui.viewmodels

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.anago.spviewer.models.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppListViewModel(private val app: Application) : AndroidViewModel(app) {
    val appList: MutableLiveData<MutableList<App>> = MutableLiveData(mutableListOf())

    fun loadAppList() {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = app.packageManager
            val apps = pm.getCInstalledApplications(0)
                .filterNot { (it.flags and ApplicationInfo.FLAG_SYSTEM) != 0 }
                .map {
                    App(
                        it.packageName,
                        it.loadLabel(pm).toString(),
                        it.loadIcon(pm)
                    )
                }.sortedBy { it.name }
            appList.postValue(apps.toMutableList())
        }
    }

    private fun PackageManager.getCInstalledApplications(flags: Long): MutableList<ApplicationInfo> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getInstalledApplications(PackageManager.ApplicationInfoFlags.of(flags))
        } else {
            @Suppress("DEPRECATION")
            getInstalledApplications(flags.toInt())
        }
    }
}