package com.anago.spviewer.ui.viewmodels

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.anago.spviewer.models.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppListViewModel(private val app: Application) : AndroidViewModel(app) {
    val appList: MutableLiveData<List<App>> = MutableLiveData()

    /* インストール済みのアプリを取得して 以下の処理後に appListを更新
        - システムアプリを除外
        - ApplicationInfoからAppに変換
        - 名前順に並び替え
    */
    fun loadAppList(onLoaded: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = app.packageManager
            val apps = pm.getCInstalledApplications(0)
                .filterNot { (it.flags and ApplicationInfo.FLAG_SYSTEM) != 0 }
                .map {
                    App(
                        it.packageName,
                        it.loadLabel(pm).toString(),
                        it.loadIcon(pm).toBitmap()
                    )
                }.sortedBy { it.name }
            appList.postValue(apps)
            onLoaded?.invoke()
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