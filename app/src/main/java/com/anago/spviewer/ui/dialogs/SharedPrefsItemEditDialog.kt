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
    private val item: SPItem = SPItem("", ""),
    private val createMode: Boolean = false,
    private val callback: Callback,
) : DialogFragment() {
    interface Callback {
        fun onDelete(key: String)
        fun onSave(oldItemKey: String, newItem: SPItem)
        fun onAddItem(newItem: SPItem)
        fun onSaveError(msg: String)
    }

    private lateinit var binding: DialogSpItemEditorBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogSpItemEditorBinding.inflate(LayoutInflater.from(requireContext()))
        return MaterialAlertDialogBuilder(requireContext()).setView(binding.root).apply {
            if (createMode) {
                setTitle("Create")
            } else {
                setTitle("Edit")
            }
        }.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val key = item.key
        val value = item.value
        var currentType: String
        var booleanValue: Boolean = false
        var anyValue: String = ""
        var setValue: Set<String> = emptySet()
        if (createMode) {
            currentType = "Boolean"
        } else {
            when (item.value) {
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

        binding.keyInput.setText(key)

        val linearLayoutManager = LinearLayoutManager(requireContext())
        val adapter = SPItemStringListAdapter(requireContext(), binding.valueSetRecyclerview)
        binding.valueSetRecyclerview.layoutManager = linearLayoutManager
        binding.valueSetRecyclerview.adapter = adapter
        binding.valueSetRecyclerview.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(), linearLayoutManager.orientation
            )
        )

        when (currentType) {
            "Boolean" -> {
                binding.valueBoolSwitch.isChecked = booleanValue
                binding.valueBoolSwitch.visibility = View.VISIBLE
                binding.valueSetLayout.visibility = View.GONE
                binding.valueInput.visibility = View.GONE
            }

            "Set" -> {
                adapter.submitStrings(setValue.toList())
                binding.valueBoolSwitch.visibility = View.GONE
                binding.valueSetLayout.visibility = View.VISIBLE
                binding.valueInput.visibility = View.GONE
            }

            else -> {
                binding.valueInput.setText(anyValue)
                binding.valueBoolSwitch.visibility = View.GONE
                binding.valueSetLayout.visibility = View.GONE
                binding.valueInput.visibility = View.VISIBLE
            }
        }

        val types = arrayOf("Boolean", "Float", "Int", "Long", "String", "Set")
        val arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, types)
        binding.typeSpinner.adapter = arrayAdapter
        binding.typeSpinner.setSelection(types.indexOf(currentType))
        binding.typeSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                currentType = types[position]
                when (currentType) {
                    "Boolean" -> {
                        binding.valueBoolSwitch.isChecked = booleanValue
                        binding.valueBoolSwitch.visibility = View.VISIBLE
                        binding.valueSetLayout.visibility = View.GONE
                        binding.valueInput.visibility = View.GONE
                    }

                    "Set" -> {
                        adapter.submitStrings(setValue.toList())
                        binding.valueBoolSwitch.visibility = View.GONE
                        binding.valueSetLayout.visibility = View.VISIBLE
                        binding.valueInput.visibility = View.GONE
                    }

                    else -> {
                        binding.valueInput.setText(anyValue)
                        binding.valueBoolSwitch.visibility = View.GONE
                        binding.valueSetLayout.visibility = View.GONE
                        binding.valueInput.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.addString.setOnClickListener {
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

        if (createMode) {
            binding.deleteBtn.visibility = View.GONE
        } else {
            binding.deleteBtn.visibility = View.VISIBLE
            binding.deleteBtn.setOnClickListener {
                callback.onDelete(item.key)
                dismiss()
            }
        }

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        binding.okBtn.setOnClickListener {
            val newItemKey = (binding.keyInput.text ?: "").toString()
            val newItemValue: Any? = when (currentType) {
                "Boolean" -> binding.valueBoolSwitch.isChecked
                "Float" -> binding.valueInput.text.toString().toFloatOrNull()
                "Int" -> binding.valueInput.text.toString().toIntOrNull()
                "Long" -> binding.valueInput.text.toString().toLongOrNull()
                "String" -> binding.valueInput.text.toString()
                "Set" -> adapter.getSet()
                else -> null
            }
            if (newItemKey.isNotBlank() && newItemValue != null) {
                val newItem = SPItem(newItemKey, newItemValue)
                if (createMode) {
                    callback.onAddItem(newItem)
                } else {
                    callback.onSave(key, newItem)
                }
            } else {
                callback.onSaveError("bad key or value")
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