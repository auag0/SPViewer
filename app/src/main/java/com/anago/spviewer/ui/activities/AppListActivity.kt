package com.anago.spviewer.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.anago.spviewer.R
import com.anago.spviewer.ui.adapters.AppListAdapter
import com.anago.spviewer.ui.customviews.SearchBar
import com.anago.spviewer.ui.viewmodels.AppListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.topjohnwu.superuser.io.SuFile
import java.io.File

class AppListActivity : AppCompatActivity() {
    private val viewModel by viewModels<AppListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applist)

        val loadingDialog = createLoadingDialog()
        loadingDialog.show()

        val appListAdapter = createAppListAdapter()
        setupRecyclerView(appListAdapter)

        viewModel.appList.observe(this) { appList ->
            appListAdapter.submitList(appList)
        }

        viewModel.loadAppList {
            loadingDialog.dismiss()
        }

        val swipeRefreshLayout: SwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadAppList {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun createLoadingDialog(): AlertDialog {
        val loadingDialog = MaterialAlertDialogBuilder(this)
            .setView(R.layout.dialog_loading)
            .setCancelable(false)
            .create()
        loadingDialog.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        return loadingDialog
    }

    private fun createAppListAdapter(): AppListAdapter {
        return AppListAdapter(this) { clickedApp ->
            val sharedPrefsDir = File(getDataDir(clickedApp.packageName), "shared_prefs")
            val sharedPrefs = getSharedPrefsList(sharedPrefsDir)
            showSelectSharedPrefs(sharedPrefsDir, sharedPrefs)
        }
    }

    private fun getDataDir(packageName: String): File {
        return if (Build.VERSION.SDK_INT >= 30) {
            File("/data_mirror/data_ce/null/0/$packageName")
        } else {
            @Suppress("SdCardPath")
            File("/data/data/$packageName")
        }
    }

    private fun getSharedPrefsList(sharedPrefDir: File): List<String> {
        return SuFile(sharedPrefDir.absolutePath).list()
            ?.filter { it.endsWith(".xml") } ?: emptyList()
    }

    private fun showSelectSharedPrefs(sharedPrefDir: File, sharedPrefs: List<String>) {
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_select_prefs_file, null, false)
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        arrayAdapter.addAll(sharedPrefs)
        val listView: ListView = dialogView.findViewById(R.id.listView)
        listView.adapter = arrayAdapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val clickedPrefs = arrayAdapter.getItem(position) ?: return@setOnItemClickListener
            val sharedPref = File(sharedPrefDir, clickedPrefs)
            val intent = Intent(this, SPEditorActivity::class.java).apply {
                putExtra("file", sharedPref)
            }
            startActivity(intent)
        }

        val searchBar: SearchBar = dialogView.findViewById(R.id.searchBar)
        searchBar.onTextChanged = { text, _, _, _ ->
            arrayAdapter.clear()
            arrayAdapter.addAll(if (text.isNullOrBlank()) {
                sharedPrefs.toList()
            } else {
                sharedPrefs
                    .filter { it.contains(text, true) }
                    .sortedBy { it.indexOf(text.toString(), ignoreCase = true) }
            })
            arrayAdapter.notifyDataSetChanged()
        }
        MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .show()
    }

    private fun setupRecyclerView(appListAdapter: AppListAdapter) {
        val linearLayoutManager = LinearLayoutManager(this)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.addItemDecoration(
            MaterialDividerItemDecoration(
                this,
                linearLayoutManager.orientation
            )
        )
        recyclerView.adapter = appListAdapter
    }
}