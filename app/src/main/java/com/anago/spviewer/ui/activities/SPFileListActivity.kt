package com.anago.spviewer.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.anago.spviewer.R
import com.anago.spviewer.models.App
import com.anago.spviewer.root.Commands.fileLists
import com.anago.spviewer.ui.activities.base.RootAccessActivity
import java.io.File

class SPFileListActivity : RootAccessActivity() {
    private val app: App by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("app", App::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("app")!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sp_files)
        title = app.name

        val spDir = File(getDataDir(app.packageName), "shared_prefs").absolutePath
        val spFiles = fileLists(spDir)
        setupListView(spDir, spFiles)
    }

    private fun setupListView(spDir: String, spFiles: List<String>) {
        val listView: ListView = findViewById(R.id.listView)
        listView.adapter = ArrayAdapter(this, R.layout.listitem_sp_file, spFiles)
        listView.setOnItemClickListener { _, _, position, _ ->
            val clickedFile = File(spDir, spFiles[position])
            startActivity(Intent(
                this, SPViewerActivity::class.java
            ).apply {
                putExtra("fileName", clickedFile.name)
                putExtra("filePath", clickedFile.absolutePath)
            })
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
}