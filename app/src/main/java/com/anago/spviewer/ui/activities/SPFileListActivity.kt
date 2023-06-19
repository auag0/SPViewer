package com.anago.spviewer.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.anago.spviewer.R
import com.anago.spviewer.root.Commands.fileLists
import com.anago.spviewer.ui.activities.base.RootAccessActivity
import java.io.File


class SPFileListActivity : RootAccessActivity() {
    private val pkgName: String by lazy {
        intent.getStringExtra("packageName")!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sp_files)

        val spDir = File(getDataDir(pkgName), "shared_prefs").absolutePath
        val spFiles = fileLists(spDir)
        setupListView(spDir, spFiles)
    }

    private fun setupListView(spDir: String, spFiles: List<String>) {
        val listView: ListView = findViewById(R.id.listView)
        listView.adapter = ArrayAdapter(this, R.layout.listitem_sp_file, spFiles)
        listView.setOnItemClickListener { _, _, position, _ ->
            val clickedFile = File(spDir, spFiles[position]).absolutePath
            startActivity(Intent(
                this, SPViewerActivity::class.java
            ).apply { putExtra("filePath", clickedFile) })
        }
    }

    private fun getDataDir(packageName: String): File {
        return if (Build.VERSION.SDK_INT >= 29) {
            File("/data_mirror/data_ce/null/0/$packageName")
        } else {
            @Suppress("SdCardPath")
            File("/data/data/$packageName")
        }
    }
}