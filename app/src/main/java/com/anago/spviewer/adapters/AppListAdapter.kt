package com.anago.spviewer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.anago.spviewer.R
import com.anago.spviewer.adapters.callbacks.AppItemDiffCallback
import com.anago.spviewer.adapters.viewholders.AppViewHolder
import com.anago.spviewer.models.AppItem
import com.bumptech.glide.Glide

class AppListAdapter(private val context: Context, private val onClick: (AppItem) -> Unit) :
    ListAdapter<AppItem, AppViewHolder>(AppItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val itemView = layoutInflater.inflate(R.layout.listitem_app, parent, false)
        return AppViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val appItem = getItem(position)

        Glide.with(context).load(appItem.icon).into(holder.icon)
        holder.name.text = appItem.name
        holder.packageName.text = appItem.packageName

        holder.itemView.setOnClickListener {
            onClick(appItem)
        }
    }
}