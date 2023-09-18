package com.anago.spviewer.applist

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.anago.spviewer.Logger.debug
import com.anago.spviewer.R
import com.anago.spviewer.applist.adapter.AppListAdapter
import com.anago.spviewer.prefs.SPEditorActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.topjohnwu.superuser.io.SuFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AppListActivity : AppCompatActivity() {
    private val viewModel: AppListViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applist)
        setSupportActionBar(findViewById(R.id.toolbar))

        val appListAdapter = AppListAdapter(this, ::onClickedAppItem)

        val appList: RecyclerView = findViewById(R.id.appList)
        appList.layoutManager = LinearLayoutManager(this)
        appList.adapter = appListAdapter

        val refreshLayout: SwipeRefreshLayout = findViewById(R.id.refreshLayout)
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = true
            viewModel.fetchInstalledAppList()
        }

        viewModel.getDisplayedAppList().observe(this) {
            if (it.isEmpty()) {
                refreshLayout.isRefreshing = true
            }
            appListAdapter.submitList(it) {
                appList.scrollToPosition(0)
                if (it.isNotEmpty()) {
                    refreshLayout.isRefreshing = false
                }
            }
        }
    }

    // よくわからないからアクセス可能なフォルダを返す
    @Suppress("SdCardPath")
    private fun getPrefsDir(packageName: String): SuFile {
        val prefs = arrayOf(
            SuFile("/data_mirror/data_ce/null/0/${packageName}/shared_prefs"),
            SuFile("/data/data/${packageName}/shared_prefs"),
            SuFile("/data/user/0/${packageName}/shared_prefs")
        )
        val found = prefs.maxBy { it.listFiles()?.size ?: -1 }
        debug("$found found prefs dir")
        return found
    }

    private fun onClickedAppItem(appItem: AppItem) {
        lifecycleScope.launch(Dispatchers.IO) {
            val sharedPrefsDir = getPrefsDir(appItem.packageName)

            val sharedPrefs = sharedPrefsDir.listFiles() ?: emptyArray()
            val sharedPrefNames = sharedPrefs.map { it.name }.toTypedArray()

            withContext(Dispatchers.Main) {
                MaterialAlertDialogBuilder(this@AppListActivity)
                    .setTitle("SharedPreferences")
                    .setItems(sharedPrefNames) { _, which ->
                        val intent = Intent(this@AppListActivity, SPEditorActivity::class.java)
                        intent.putExtra(SPEditorActivity.EXTRA_PREF_FILE_PATH, sharedPrefs[which])
                        startActivity(intent)
                    }.show()
            }
        }
    }
}