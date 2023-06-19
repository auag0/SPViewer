package com.anago.spviewer.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anago.spviewer.R
import com.anago.spviewer.models.App
import com.anago.spviewer.ui.activities.SPFileListActivity
import com.bumptech.glide.Glide

class AppListAdapter(private val context: Context) : ListAdapter<App, AppListAdapter.ViewHolder>(object : DiffUtil.ItemCallback<App>() {
    override fun areItemsTheSame(oldItem: App, newItem: App): Boolean {
        return oldItem.packageName == newItem.packageName
    }

    override fun areContentsTheSame(oldItem: App, newItem: App): Boolean {
        return oldItem == newItem
    }
}) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val name: TextView = itemView.findViewById(R.id.name)
        val packageName: TextView = itemView.findViewById(R.id.packageName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.listitem_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = getItem(position)
        holder.name.text = app.name
        holder.packageName.text = app.packageName
        Glide.with(context).load(app.icon).into(holder.icon)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, SPFileListActivity::class.java).apply {
                putExtra("app", app)
            }
            context.startActivity(intent)
        }
    }
}