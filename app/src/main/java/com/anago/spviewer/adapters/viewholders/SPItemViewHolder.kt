package com.anago.spviewer.adapters.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.anago.spviewer.R
import com.google.android.material.textview.MaterialTextView

class SPItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val key: MaterialTextView = itemView.findViewById(R.id.key)
    val value: MaterialTextView = itemView.findViewById(R.id.value)
}
