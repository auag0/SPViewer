package com.anago.spviewer.ui.activities

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anago.spviewer.MyApp.Companion.makeToast
import com.anago.spviewer.R
import com.anago.spviewer.models.SPItem
import com.anago.spviewer.ui.adapters.SPItemListAdapter
import com.anago.spviewer.ui.dialogs.SharedPrefsItemEditDialog
import com.anago.spviewer.ui.viewmodels.SPEditorViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class SPEditorActivity : AppCompatActivity() {
    private val mXmlFile: File by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("file", File::class.java) as File
        } else {
            @Suppress("DEPRECATION") intent.getSerializableExtra("file") as File
        }
    }

    private val mViewModel: SPEditorViewModel by viewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sp_viewer)
        title = mXmlFile.name

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            showAddOrEditItemDialog(createMode = true)
        }

        val spItemListAdapter = SPItemListAdapter(onClickItem = {
            showAddOrEditItemDialog(it, false)
        })

        setupRecyclerView(spItemListAdapter)

        mViewModel.getItems().observe(this) {
            spItemListAdapter.submitItems(it)
        }

        mViewModel.loadSharedPrefsFile(mXmlFile)

        onBackPressedDispatcher.addCallback(this, true) {
            if (mViewModel.isModified()) {
                showCheckSaveDialog()
            } else {
                finish()
            }
        }
    }

    private fun setupRecyclerView(adapter: SPItemListAdapter) {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun showAddOrEditItemDialog(
        item: SPItem = SPItem("", ""),
        createMode: Boolean = false
    ) {
        val dialog = SharedPrefsItemEditDialog.newInstance(
            item,
            createMode,
            object : SharedPrefsItemEditDialog.Callback {
                override fun onDelete(key: String) {
                    mViewModel.deleteItem(key)
                }

                override fun onSave(oldItemKey: String, newItem: SPItem) {
                    mViewModel.changeItem(oldItemKey, newItem)
                }

                override fun onAddItem(newItem: SPItem) {
                    mViewModel.addItem(newItem)
                }

                override fun onSaveError(msg: String) {
                    makeToast(this@SPEditorActivity, msg)
                }
            })
        dialog.show(supportFragmentManager, null)
    }

    private fun showCheckSaveDialog() {
        MaterialAlertDialogBuilder(this@SPEditorActivity)
            .setTitle("Save Changes Confirmation")
            .setMessage("Do you want to save the changes?")
            .setNegativeButton("Cancel") { _, _ ->
                finish()
            }
            .setPositiveButton(
                "Save"
            ) { _, _ ->
                mViewModel.saveItemsToFile(mXmlFile) {
                    finish()
                }
            }.show()
    }
}