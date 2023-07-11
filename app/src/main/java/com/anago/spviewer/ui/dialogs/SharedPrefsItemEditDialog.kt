package com.anago.spviewer.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anago.spviewer.R
import com.anago.spviewer.databinding.DialogSpItemEditorBinding
import com.anago.spviewer.models.SPItem
import com.anago.spviewer.ui.adapters.SPItemStringListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.textfield.TextInputEditText

class SharedPrefsItemEditDialog(
    private val mItem: SPItem = SPItem("", ""),
    private val mCreateMode: Boolean = false,
    private val mCallback: Callback,
) : DialogFragment() {
    interface Callback {
        fun onDelete(key: String)
        fun onSave(oldItemKey: String, newItem: SPItem)
        fun onAddItem(newItem: SPItem)
        fun onSaveError(msg: String)
    }

    private lateinit var mBinding: DialogSpItemEditorBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBinding = DialogSpItemEditorBinding.inflate(LayoutInflater.from(requireContext()))
        return MaterialAlertDialogBuilder(requireContext()).setView(mBinding.root).apply {
            if (mCreateMode) {
                setTitle("Create")
            } else {
                setTitle("Edit")
            }
        }.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val key = mItem.key
        val value = mItem.value
        var currentType: String
        var booleanValue: Boolean = false
        var anyValue: String = ""
        var setValue: Set<String> = emptySet()
        if (mCreateMode) {
            currentType = "Boolean"
        } else {
            when (mItem.value) {
                is Boolean -> {
                    booleanValue = value.toString().toBoolean()
                    currentType = "Boolean"
                }

                is Float -> {
                    anyValue = value.toString()
                    currentType = "Float"
                }

                is Int -> {
                    anyValue = value.toString()
                    currentType = "Int"
                }

                is Long -> {
                    anyValue = value.toString()
                    currentType = "Long"
                }

                is String -> {
                    anyValue = value.toString()
                    currentType = "String"
                }

                is Set<*> -> {
                    setValue = value as Set<String>
                    currentType = "Set"
                }

                else -> return
            }
        }

        mBinding.keyInput.setText(key)

        val linearLayoutManager = LinearLayoutManager(requireContext())
        val adapter = SPItemStringListAdapter(requireContext(), mBinding.valueSetRecyclerview)
        mBinding.valueSetRecyclerview.layoutManager = linearLayoutManager
        mBinding.valueSetRecyclerview.adapter = adapter
        mBinding.valueSetRecyclerview.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(), linearLayoutManager.orientation
            )
        )

        when (currentType) {
            "Boolean" -> {
                mBinding.valueBoolSwitch.isChecked = booleanValue
                mBinding.valueBoolSwitch.visibility = View.VISIBLE
                mBinding.valueSetLayout.visibility = View.GONE
                mBinding.valueInput.visibility = View.GONE
            }

            "Set" -> {
                adapter.submitStrings(setValue.toList())
                mBinding.valueBoolSwitch.visibility = View.GONE
                mBinding.valueSetLayout.visibility = View.VISIBLE
                mBinding.valueInput.visibility = View.GONE
            }

            else -> {
                mBinding.valueInput.setText(anyValue)
                mBinding.valueBoolSwitch.visibility = View.GONE
                mBinding.valueSetLayout.visibility = View.GONE
                mBinding.valueInput.visibility = View.VISIBLE
            }
        }

        val types = arrayOf("Boolean", "Float", "Int", "Long", "String", "Set")
        val arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, types)
        mBinding.typeSpinner.adapter = arrayAdapter
        mBinding.typeSpinner.setSelection(types.indexOf(currentType))
        mBinding.typeSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                currentType = types[position]
                when (currentType) {
                    "Boolean" -> {
                        mBinding.valueBoolSwitch.isChecked = booleanValue
                        mBinding.valueBoolSwitch.visibility = View.VISIBLE
                        mBinding.valueSetLayout.visibility = View.GONE
                        mBinding.valueInput.visibility = View.GONE
                    }

                    "Set" -> {
                        adapter.submitStrings(setValue.toList())
                        mBinding.valueBoolSwitch.visibility = View.GONE
                        mBinding.valueSetLayout.visibility = View.VISIBLE
                        mBinding.valueInput.visibility = View.GONE
                    }

                    else -> {
                        mBinding.valueInput.setText(anyValue)
                        mBinding.valueBoolSwitch.visibility = View.GONE
                        mBinding.valueSetLayout.visibility = View.GONE
                        mBinding.valueInput.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        mBinding.addString.setOnClickListener {
            val editText = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_string, null, false) as TextInputEditText
            MaterialAlertDialogBuilder(requireContext()).setTitle("Add String").setView(editText)
                .setPositiveButton("add") { _, _ ->
                    val oldValue = setValue.toMutableSet()
                    oldValue.add((editText.text ?: "").toString())
                    setValue = oldValue
                    adapter.submitStrings(setValue.toList())
                }.setNegativeButton("cancel", null).show()
        }

        if (mCreateMode) {
            mBinding.deleteBtn.visibility = View.GONE
        } else {
            mBinding.deleteBtn.visibility = View.VISIBLE
            mBinding.deleteBtn.setOnClickListener {
                mCallback.onDelete(mItem.key)
                dismiss()
            }
        }

        mBinding.cancelBtn.setOnClickListener {
            dismiss()
        }

        mBinding.okBtn.setOnClickListener {
            val newItemKey = (mBinding.keyInput.text ?: "").toString()
            val newItemValue: Any? = when (currentType) {
                "Boolean" -> mBinding.valueBoolSwitch.isChecked
                "Float" -> mBinding.valueInput.text.toString().toFloatOrNull()
                "Int" -> mBinding.valueInput.text.toString().toIntOrNull()
                "Long" -> mBinding.valueInput.text.toString().toLongOrNull()
                "String" -> mBinding.valueInput.text.toString()
                "Set" -> adapter.getSet()
                else -> null
            }
            if (newItemKey.isNotBlank() && newItemValue != null) {
                val newItem = SPItem(newItemKey, newItemValue)
                if (mCreateMode) {
                    mCallback.onAddItem(newItem)
                } else {
                    mCallback.onSave(key, newItem)
                }
            } else {
                mCallback.onSaveError("bad key or value")
            }
            dismiss()
        }
    }

    companion object {
        fun newInstance(
            item: SPItem,
            createMode: Boolean = false,
            callback: Callback
        ): SharedPrefsItemEditDialog {
            return SharedPrefsItemEditDialog(item, createMode, callback)
        }
    }
}