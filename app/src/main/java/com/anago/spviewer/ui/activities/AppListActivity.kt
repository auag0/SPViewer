package com.anago.spviewer.ui.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anago.spviewer.R
import com.anago.spviewer.ui.adapters.AppListAdapter
import com.anago.spviewer.ui.viewmodels.AppListViewModel

class AppListActivity : AppCompatActivity() {
    private val viewModel = viewModels<AppListViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applist)

        val appListAdapter = AppListAdapter(this)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = appListAdapter

        viewModel.value.appList.observe(this) { appList ->
            appListAdapter.submitList(appList)
        }

        viewModel.value.loadAppList()
    }
}