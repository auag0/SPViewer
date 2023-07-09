package com.anago.spviewer.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anago.spviewer.R
import com.anago.spviewer.models.SPItem

class SPItemListAdapter(
    private val onClickItem: (SPItem) -> Unit
) : RecyclerView.Adapter<SPItemListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeText: TextView = itemView.findViewById(R.id.typeText)
        val keyText: TextView = itemView.findViewById(R.id.keyText)
        val valueText: TextView = itemView.findViewById(R.id.valueText)
    }

    private var mItems: List<SPItem> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun submitItems(items: List<SPItem>) {
        mItems = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.listitem_sp_item, parent, false)
    )

    override fun getItemCount(): Int = mItems.size

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mItems[position]
        // LinkedHashSetやSingletonSetをSetに統一
        val type = when (item.value) {
            is Set<*> -> "Set"
            else -> item.value.javaClass.simpleName
        }

        holder.typeText.text = type
        holder.keyText.text = item.key
        holder.valueText.text = item.value.toString()

        holder.itemView.setOnClickListener {
            onClickItem(item)
        }
    }
}