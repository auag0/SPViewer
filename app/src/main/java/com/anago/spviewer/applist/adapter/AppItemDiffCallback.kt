package com.anago.spviewer.applist.adapter

import androidx.core.util.ObjectsCompat
import androidx.recyclerview.widget.DiffUtil
import com.anago.spviewer.applist.AppItem

class AppItemDiffCallback : DiffUtil.ItemCallback<AppItem>() {
    override fun areItemsTheSame(oldItem: AppItem, newItem: AppItem): Boolean {
        return oldItem.packageName == newItem.packageName
    }

    override fun areContentsTheSame(oldItem: AppItem, newItem: AppItem): Boolean {
        return ObjectsCompat.equals(oldItem, newItem)
    }
}