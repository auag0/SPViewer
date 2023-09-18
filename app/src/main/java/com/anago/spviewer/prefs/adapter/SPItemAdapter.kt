package com.anago.spviewer.prefs.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.anago.spviewer.R
import com.anago.spviewer.prefs.SPItem

class SPItemAdapter(
    private val context: Context,
    private val onClicked: (SPItem) -> Unit,
    private val onLongClicked: (SPItem) -> Unit
) : ListAdapter<SPItem, SPItemViewHolder>(SPItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SPItemViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val itemView = layoutInflater.inflate(R.layout.listitem_spitem, parent, false)
        return SPItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SPItemViewHolder, position: Int) {
        val spItem = getItem(position)

        holder.key.text = spItem.key
        holder.value.text = spItem.value.toString()

        holder.itemView.setOnClickListener {
            onClicked(spItem)
        }

        holder.itemView.setOnLongClickListener {
            onLongClicked(spItem)
            true
        }
    }
}