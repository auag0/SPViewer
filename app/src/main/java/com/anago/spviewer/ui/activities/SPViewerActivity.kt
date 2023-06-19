package com.anago.spviewer.ui.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anago.spviewer.R
import com.anago.spviewer.SPParser
import com.anago.spviewer.ui.activities.base.RootAccessActivity
import com.anago.spviewer.ui.adapters.SPItemListAdapter

class SPViewerActivity : RootAccessActivity() {
    private val filePath: String by lazy {
        intent.getStringExtra("filePath")!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sp_viewer)

        val parser = SPParser(filePath, true)

        val spItemListAdapter = SPItemListAdapter(parser.getAllByList())
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = spItemListAdapter
    }
}