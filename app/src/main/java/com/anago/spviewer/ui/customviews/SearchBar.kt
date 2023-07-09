package com.anago.spviewer.ui.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import com.anago.spviewer.R

class SearchBar @JvmOverloads constructor(
    context: Context, attr: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : LinearLayout(context, attr, defStyleAttr, defStyleRes) {
    init {
        LayoutInflater.from(context).inflate(R.layout.customview_search_bar, this)
        val editText: EditText = findViewById(R.id.editText)
        editText.addTextChangedListener(
            onTextChanged = { text, start, before, count ->
                onTextChanged?.invoke(text, start, before, count)
            }
        )
    }

    var onTextChanged: ((CharSequence?, Int, Int, Int) -> Unit)? = null
}