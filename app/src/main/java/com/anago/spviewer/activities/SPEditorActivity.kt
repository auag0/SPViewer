package com.anago.spviewer.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anago.spviewer.R
import com.anago.spviewer.adapters.SPItemAdapter
import com.anago.spviewer.dialogs.SPItemEditDialog
import com.anago.spviewer.models.SPItem
import com.anago.spviewer.viewmodels.SPEditorViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SPEditorActivity : AppCompatActivity(), SPItemEditDialog.Listener {
    private val viewModel: SPEditorViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speditor)
        setSupportActionBar(findViewById(R.id.toolbar))

        val spItemAdapter = SPItemAdapter(
            this,
            onClicked = ::showSPItemEditDialog,
            onLongClicked = ::showSPItemDeleteConfirmDialog
        )
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = spItemAdapter
        recyclerView.itemAnimator = null

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            showSPItemCreateDialog()
        }

        onBackPressedDispatcher.addCallback(this) {
            if (viewModel.isModified()) {
                showSaveConfirmDialog()
            } else {
                finish()
            }
        }

        viewModel.prefFile.observe(this) { prefFile ->
            supportActionBar?.title = prefFile?.name
        }

        viewModel.spItems.observe(this) {
            spItemAdapter.submitList(it)
        }
    }

    private fun showSPItemCreateDialog() {
        SPItemEditDialog.newInstance(true, null).also {
            it.show(supportFragmentManager, null)
        }
    }

    private fun showSPItemEditDialog(spItem: SPItem) {
        SPItemEditDialog.newInstance(false, spItem).also {
            it.show(supportFragmentManager, null)
        }
    }

    private fun showSPItemDeleteConfirmDialog(spItem: SPItem) {
        MaterialAlertDialogBuilder(this).apply {
            setTitle(R.string.dialog_edit_delete_confirm_title)
            setMessage(R.string.dialog_edit_delete_confirm_message)
            setPositiveButton(R.string.dialog_edit_delete_confirm_delete) { _, _ ->
                viewModel.deleteSPItem(spItem)
            }
            setNegativeButton(R.string.dialog_edit_delete_confirm_cancel, null)
        }.show()
    }

    private fun showSaveConfirmDialog() {
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
    }

    override fun onSPItemEdited(oldSPItem: SPItem, newSPItem: SPItem) {
        viewModel.editSPItem(oldSPItem.key, newSPItem)?.let { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSPItemCreated(newSPItem: SPItem) {
        viewModel.createSPItem(newSPItem)?.let { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_PREF_FILE_PATH = "pref_file_path"
    }
}