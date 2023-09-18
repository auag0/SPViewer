package com.anago.spviewer.prefs.adapter

import androidx.core.util.ObjectsCompat
import androidx.recyclerview.widget.DiffUtil
import com.anago.spviewer.prefs.SPItem

class SPItemDiffCallback : DiffUtil.ItemCallback<SPItem>() {
    override fun areItemsTheSame(oldItem: SPItem, newItem: SPItem): Boolean {
        return oldItem.key == newItem.key
    }

    override fun areContentsTheSame(oldItem: SPItem, newItem: SPItem): Boolean {
        return ObjectsCompat.equals(oldItem, newItem)
    }
}
