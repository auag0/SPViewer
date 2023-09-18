package com.anago.spviewer.prefs.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.anago.spviewer.Logger
import com.anago.spviewer.R
import com.anago.spviewer.prefs.SPItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SPItemEditDialog(
    private val isCreateMode: Boolean,
    private val oldSPItem: SPItem?,
    private val onEdited: ((SPItem, SPItem) -> String?)? = null,
    private val onCreated: ((SPItem) -> String?)? = null
) : DialogFragment() {
    private lateinit var dialogView: View
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = layoutInflater.inflate(R.layout.dialog_edit_spitem, null, false)

        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(dialogView)
            setNegativeButton(R.string.dialog_edit_cancel) { _, _ ->
                dismiss()
            }
            if (isCreateMode) {
                setTitle(R.string.dialog_edit_create)
                setPositiveButton(R.string.dialog_edit_create, null)
            } else {
                setTitle(R.string.dialog_edit_edit)
                setPositiveButton(R.string.dialog_edit_edit, null)
            }
        }.create()

        dialog.setOnShowListener {
            val positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveBtn.setOnClickListener {
                val newSPItem = createSPItem()
                var result: String? = null
                if (isCreateMode) {
                    if (newSPItem != null) {
                        result = onCreated?.invoke(newSPItem)
                    } else {
                        Toast.makeText(requireContext(), "Create failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (newSPItem != null && oldSPItem != null) {
                        result = onEdited?.invoke(oldSPItem, newSPItem)
                    } else {
                        Toast.makeText(requireContext(), "Edit failed", Toast.LENGTH_SHORT).show()
                    }
                }
                if (result.isNullOrBlank()) {
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
                }
            }
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return dialogView
    }

    private lateinit var keyInputLayout: TextInputLayout
    private lateinit var keyEditText: TextInputEditText

    private lateinit var valueInputLayout: TextInputLayout
    private lateinit var valueEditText: TextInputEditText

    private lateinit var typeInputLayout: TextInputLayout
    private lateinit var typeAutoComplete: MaterialAutoCompleteTextView

    private val types = arrayOf(
        "Boolean", "Float", "Int", "Long", "String"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        keyInputLayout = view.findViewById(R.id.keyInputLayout)
        keyEditText = view.findViewById(R.id.keyEditText)

        valueInputLayout = view.findViewById(R.id.valueInputLayout)
        valueEditText = view.findViewById(R.id.valueEditText)

        typeInputLayout = view.findViewById(R.id.typeInputLayout)
        typeAutoComplete = view.findViewById(R.id.typeAutoComplete)

        val typeAdapter = ArrayAdapter(
            requireContext(),
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            types
        )

        typeAutoComplete.setAdapter(typeAdapter)

        if (isCreateMode || oldSPItem == null) {
            typeAutoComplete.setText(
                types[0], false
            )
        } else {
            keyEditText.setText(oldSPItem.key)
            valueEditText.setText(oldSPItem.value.toString())
            typeAutoComplete.setText(
                when (oldSPItem.value) {
                    is Boolean -> types[0]
                    is Float -> types[1]
                    is Int -> types[2]
                    is Long -> types[3]
                    is String -> types[4]
                    else -> types[4]
                }, false
            )
        }

        valueEditText.addTextChangedListener(onTextChanged = { _, _, _, _ ->
            checkValueWithTypeAndDisplay()
        })

        typeAutoComplete.addTextChangedListener(onTextChanged = { _, _, _, _ ->
            checkValueWithTypeAndDisplay()
        })
    }

    private fun checkValueWithTypeAndDisplay() {
        if (checkValueWithType()) {
            valueInputLayout.error = null
            valueInputLayout.isErrorEnabled = false
        } else {
            valueInputLayout.isErrorEnabled = true
            valueInputLayout.error = "Please value of correct type"
        }
    }

    private fun checkValueWithType(): Boolean {
        val selectedType = types.indexOf(typeAutoComplete.text.toString())
        val curValue = valueEditText.text.toString()
        try {
            when (selectedType) {
                0 -> curValue.toBooleanEasy()
                1 -> curValue.toFloat()
                2 -> curValue.toInt()
                3 -> curValue.toLong()
            }
        } catch (e: NumberFormatException) {
            Logger.error(e.message)
            return false
        } catch (e: IllegalArgumentException) {
            Logger.error(e.message)
            return false
        }
        return true
    }

    private fun createSPItem(): SPItem? {
        if (checkValueWithType()) {
            val key = keyEditText.text
            if (key.isNullOrBlank()) {
                return null
            }
            val value = valueEditText.text.toString()
            val type = types.indexOf(typeAutoComplete.text.toString())
            return SPItem(
                key = key.toString(), value = when (type) {
                    0 -> value.toBooleanEasy()
                    1 -> value.toFloat()
                    2 -> value.toInt()
                    3 -> value.toLong()
                    else -> value
                }
            )
        } else {
            return null
        }
    }

    private fun Any.toBooleanEasy(): Boolean {
        return when (this.toString().lowercase()) {
            "true", "1" -> true
            "false", "0" -> false
            else -> throw IllegalArgumentException()
        }
    }
}