package com.anago.spviewer.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.anago.spviewer.R
import com.anago.spviewer.adapters.AppListAdapter
import com.anago.spviewer.models.AppItem
import com.anago.spviewer.utils.FileUtils
import com.anago.spviewer.viewmodels.AppListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AppListActivity : AppCompatActivity() {
    private val viewModel: AppListViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applist)
        setSupportActionBar(findViewById(R.id.toolbar))

        val appListAdapter = AppListAdapter(this, ::showClickedActionDialog)
        val appList: RecyclerView = findViewById(R.id.appList)
        appList.layoutManager = LinearLayoutManager(this)
        appList.adapter = appListAdapter

        val refreshLayout: SwipeRefreshLayout = findViewById(R.id.refreshLayout)
        refreshLayout.setOnRefreshListener {
            viewModel.fetchInstalledAppList()
        }

        viewModel.isRefreshing.observe(this) {
            refreshLayout.isRefreshing = it
        }

        viewModel.displayedAppList.observe(this) {
            appListAdapter.submitList(it) {
                appList.scrollToPosition(0)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.applist, menu)

        val searchView = menu.findItem(R.id.searchView).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.searchQuery = newText
                return true
            }
        })
        return true
    }

    private fun showClickedActionDialog(appItem: AppItem) {
        MaterialAlertDialogBuilder(this)
            .setItems(arrayOf("SharedPreferences")) { _, which ->
                when (which) {
                    0 -> showPRefsDialog(appItem)
                }
            }.show()
    }

    private fun showPRefsDialog(appItem: AppItem) {
        val sharedPrefs = FileUtils.getFilesInDataFile(appItem.packageName, "shared_prefs", ".xml")
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