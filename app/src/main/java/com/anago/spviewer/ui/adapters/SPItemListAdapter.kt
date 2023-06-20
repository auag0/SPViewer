package com.anago.spviewer.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anago.spviewer.R

class SPItemListAdapter(private val items: List<Pair<String, Any?>>) : RecyclerView.Adapter<SPItemListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeText: TextView = itemView.findViewById(R.id.typeText)
        val keyText: TextView = itemView.findViewById(R.id.keyText)
        val valueText: TextView = itemView.findViewById(R.id.valueText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_sp_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val key = item.first
        val value = item.second
        // LinkedHashSetやSingletonSetをSetに統一
        val type = when (value) {
            is Set<*> -> "Set"
            else -> value?.javaClass?.simpleName ?: "Unknown"
        }

        holder.typeText.text = type
        holder.keyText.text = key
        holder.valueText.text = value.toString()
    }

    override fun getItemCount(): Int {
        return items.size
    }
}