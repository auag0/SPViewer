package com.anago.spviewer.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.anago.spviewer.R
import com.anago.spviewer.compat.BundleCompat.getCSerializable
import com.anago.spviewer.models.SPItem
import com.anago.spviewer.utils.Logger
import com.anago.spviewer.utils.NumberUtils.toBooleanEasy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SPItemEditDialog : DialogFragment() {
    private var oldSPItem: SPItem? = null
    private var isCreateMode: Boolean = false
    private lateinit var listener: Listener
    private lateinit var dialogView: View
    private lateinit var keyInputLayout: TextInputLayout
    private lateinit var keyEditText: TextInputEditText
    private lateinit var valueInputLayout: TextInputLayout
    private lateinit var valueEditText: TextInputEditText
    private lateinit var typeInputLayout: TextInputLayout
    private lateinit var typeAutoComplete: MaterialAutoCompleteTextView

    private val types = arrayOf("Boolean", "Float", "Int", "Long", "String")

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as Listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        oldSPItem = arguments?.getCSerializable(ARGS_OLD_SPITEM)
        isCreateMode = arguments?.getBoolean(ARGS_IS_CREATE_MODE, false)!!
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = layoutInflater.inflate(R.layout.dialog_edit_spitem, null, false)

        val textResId = if (isCreateMode) R.string.dialog_edit_create else R.string.dialog_edit_edit
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setTitle(textResId)
            .setPositiveButton(textResId) { _, _ ->
                val newSPItem = createSPItem() ?: return@setPositiveButton
                if (isCreateMode) {
                    listener.onSPItemCreated(newSPItem)
                } else {
                    if (oldSPItem == null) {
                        return@setPositiveButton
                    }
                    listener.onSPItemEdited(oldSPItem!!, newSPItem)
                }
            }
            .setNegativeButton(R.string.dialog_edit_cancel) { _, _ ->
                dismiss()
            }
            .create()

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return dialogView
    }
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
            keyEditText.setText(oldSPItem!!.key)
            valueEditText.setText(oldSPItem!!.value.toString())
            typeAutoComplete.setText(
                when (oldSPItem!!.value) {
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
        val value = valueEditText.text.toString()
        val selectedType = typeAutoComplete.text.toString()

        if (isValidValueWithType(value, selectedType)) {
            valueInputLayout.error = null
            valueInputLayout.isErrorEnabled = false
        } else {
            valueInputLayout.isErrorEnabled = true
            valueInputLayout.error = "Please value of correct type"
        }
    }

    private fun isValidValueWithType(value: String, selectedType: String?): Boolean {
        try {
            when (selectedType) {
                "Boolean" -> value.toBooleanEasy()
                "Float" -> value.toFloat()
                "Int" -> value.toInt()
                "Long" -> value.toLong()
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
        val key = keyEditText.text.toString()
        val value = valueEditText.text.toString()
        val selectedType = typeAutoComplete.text.toString()

        if (!isValidValueWithType(value, selectedType)) {
            return null
        }
        if (key.isBlank()) {
            return null
        }

        return SPItem(
            key = key,
            value = when (selectedType) {
                "Boolean" -> value.toBooleanEasy()
                "Float" -> value.toFloat()
                "Int" -> value.toInt()
                "Long" -> value.toLong()
                else -> value
            }
        )
    }

    interface Listener {
        fun onSPItemEdited(oldSPItem: SPItem, newSPItem: SPItem)
        fun onSPItemCreated(newSPItem: SPItem)
    }

    companion object {
        private const val ARGS_OLD_SPITEM = "old_spitem"
        private const val ARGS_IS_CREATE_MODE = "is_create_mode"

        fun newInstance(isCreateMode: Boolean, oldSPItem: SPItem? = null): SPItemEditDialog {
            return SPItemEditDialog().apply {
                arguments = Bundle().apply {
                    putBoolean(ARGS_IS_CREATE_MODE, isCreateMode)
                    putSerializable(ARGS_OLD_SPITEM, oldSPItem)
                }
            }
        }
    }
}