package com.anago.spviewer.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.anago.spviewer.R
import com.anago.spviewer.adapters.AppListAdapter
import com.anago.spviewer.models.AppItem
import com.anago.spviewer.viewmodels.AppListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.topjohnwu.superuser.io.SuFile
import java.io.FilenameFilter

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

    // よくわからないからファイル数が一番多いフォルダを返す
    @Suppress("SdCardPath")
    private fun getDataDir(packageName: String): SuFile {
        val prefs = arrayOf(
            SuFile("/data_mirror/data_ce/null/0/${packageName}"),
            SuFile("/data/data/${packageName}"),
            SuFile("/data/user/0/${packageName}")
        )
        return prefs.maxBy { it.listFiles()?.size ?: -1 }
    }

    private fun onClickedAppItem(appItem: AppItem) {
        showClickedActionDialog(appItem)
    }

    private fun showClickedActionDialog(appItem: AppItem) {
        MaterialAlertDialogBuilder(this)
            .setItems(arrayOf("SharedPreferences")) { _, which ->
                when (which) {
                    0 -> showPRefsDialog(appItem)
                }
            }.show()
    }

    private fun getFilesInDataFile(
        packageName: String,
        child: String,
        suffix: String
    ): Array<SuFile> {
        val dataDirectory = getDataDir(packageName)
        val directory = SuFile(dataDirectory, child)

        val filter = FilenameFilter { _, name ->
            return@FilenameFilter name.endsWith(suffix, true)
        }

        return directory.listFiles(filter) ?: emptyArray()
    }

    private fun showPRefsDialog(appItem: AppItem) {
        val sharedPrefs = getFilesInDataFile(appItem.packageName, "shared_prefs", ".xml")
        val sharedPrefNames = sharedPrefs.map { it.name }.toTypedArray()

        MaterialAlertDialogBuilder(this@AppListActivity)
            .setTitle("SharedPreferences")
            .setItems(sharedPrefNames) { _, which ->
                val intent = Intent(this@AppListActivity, SPEditorActivity::class.java)
                intent.putExtra(SPEditorActivity.EXTRA_PREF_FILE_PATH, sharedPrefs[which])
                startActivity(intent)
            }.show()
    }
}