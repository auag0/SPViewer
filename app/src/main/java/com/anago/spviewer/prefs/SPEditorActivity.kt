package com.anago.spviewer.prefs

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anago.spviewer.R
import com.anago.spviewer.compat.IntentCompat.getCSerializableExtra
import com.anago.spviewer.prefs.adapter.SPItemAdapter
import com.anago.spviewer.prefs.dialog.SPItemEditDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.topjohnwu.superuser.io.SuFile

class SPEditorActivity : AppCompatActivity() {
    private val viewModel: SPEditorViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speditor)
        setSupportActionBar(findViewById(R.id.toolbar))

        val prefFile: SuFile = intent.getCSerializableExtra(EXTRA_PREF_FILE_PATH)
        viewModel.setPrefFile(prefFile)

        supportActionBar?.title = prefFile.name

        val spItemAdapter = SPItemAdapter(this, ::onClicked, ::onLongClicked)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = spItemAdapter

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            SPItemEditDialog(true, null, onCreated = { spItem ->
                return@SPItemEditDialog viewModel.createSPItem(spItem)
            }).show(supportFragmentManager, null)
        }

        viewModel.getSPItems().observe(this) {
            spItemAdapter.submitList(it)
        }

        onBackPressedDispatcher.addCallback(this) {
            if (viewModel.isModified()) {
                MaterialAlertDialogBuilder(this@SPEditorActivity).apply {
                    setTitle(R.string.dialog_edit_exit_confirm_title)
                    setMessage(R.string.dialog_edit_exit_confirm_message)
                    setPositiveButton(R.string.dialog_edit_exit_confirm_save) { _, _ ->
                        viewModel.savePrefFile {
                            finish()
                        }
                    }
                    setNegativeButton(R.string.dialog_edit_exit_confirm_later, null)
                    setNeutralButton(R.string.dialog_edit_exit_confirm_exit) { _, _ ->
                        finish()
                    }
                }.show()
            } else {
                finish()
            }
        }
    }

    private fun onClicked(spItem: SPItem) {
        SPItemEditDialog(false, spItem, onEdited = { oldItem, newItem ->
            return@SPItemEditDialog viewModel.editSPItem(oldItem.key, newItem)
        }).show(supportFragmentManager, null)
    }

    private fun onLongClicked(spItem: SPItem) {
        MaterialAlertDialogBuilder(this).apply {
            setTitle(R.string.dialog_edit_delete_confirm_title)
            setMessage(R.string.dialog_edit_delete_confirm_message)
            setPositiveButton(R.string.dialog_edit_delete_confirm_delete) { _, _ ->
                viewModel.deleteSPItem(spItem)
            }
            setNegativeButton(R.string.dialog_edit_delete_confirm_cancel, null)
        }.show()
    }

    companion object {
        const val EXTRA_PREF_FILE_PATH = "pref_file_path"
    }
}