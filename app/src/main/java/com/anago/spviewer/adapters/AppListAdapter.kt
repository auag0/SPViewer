package com.anago.spviewer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.anago.spviewer.R
import com.anago.spviewer.adapters.callbacks.AppItemDiffCallback
import com.anago.spviewer.adapters.viewholders.AppViewHolder
import com.anago.spviewer.models.AppItem
import com.anago.spviewer.utils.Logger
import com.bumptech.glide.Glide
import me.zhanghai.android.fastscroll.PopupTextProvider

class AppListAdapter(private val context: Context, private val onClick: (AppItem) -> Unit) :
    ListAdapter<AppItem, AppViewHolder>(AppItemDiffCallback()), PopupTextProvider {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val itemView = layoutInflater.inflate(R.layout.listitem_app, parent, false)
        return AppViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val appItem = getItem(position)

        Glide.with(context)
            .load(appItem.packageInfo)
            .error(context.packageManager.defaultActivityIcon)
            .into(holder.icon)
        holder.name.text = appItem.name
        holder.packageName.text = appItem.packageName

        holder.itemView.setOnClickListener {
            onClick(appItem)
        }
    }

    override fun getPopupText(view: View, position: Int): CharSequence {
        return try {
            getItem(position).name.substring(0, 1).uppercase()
        } catch (e: ArrayIndexOutOfBoundsException) {
            Logger.error(e.message)
            e.printStackTrace()
            ""
        }
    }
}